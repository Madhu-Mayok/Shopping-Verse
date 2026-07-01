package com.example.shoppingverse.service;

import com.example.shoppingverse.Enum.ProductStatus;
import com.example.shoppingverse.dto.request.OrderRequestDto;
import com.example.shoppingverse.dto.response.OrderResponseDto;
import com.example.shoppingverse.exception.CustomerNotFoundException;
import com.example.shoppingverse.exception.InsufficientQuantityException;
import com.example.shoppingverse.exception.InvalidCardException;
import com.example.shoppingverse.exception.ProductNotFoundException;
import com.example.shoppingverse.model.*;
import com.example.shoppingverse.repository.*;
import com.example.shoppingverse.transformer.ItemTransformer;
import com.example.shoppingverse.transformer.OrderTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class OrderService {

    private static final Logger log =  LoggerFactory.getLogger(OrderService.class);

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    CardRespository cardRespository;

    @Autowired
    OrderEntityRepository orderEntityRepository;

    @Autowired
    JavaMailSender javaMailSender;

    @Autowired
    CardService cardService;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private CartRepository cartRepository;


    //Suppose:
    //product quantity reduced
    //and then:
    //order save fails

    //Now:
    //Stock reduced
    //Order not created
    //Database becomes inconsistent.

    //solution --@Transcational
    //    Why?
    //    If anything fails:
    //            throw new RuntimeException(...)
    //    Spring automatically:
    //    ROLLBACK
    //    Everything returns to previous state.
    //    No partial updates.
    //@Transactional creates a database transaction boundary. If all operations succeed, changes are committed.
    // If any exception occurs, all database changes are rolled back, ensuring data consistency.

    @Transactional
    public OrderResponseDto placeOrder(OrderRequestDto orderRequestDto) {

        log.info("Order request received for customer: {} and productId: {}",
                orderRequestDto.getCustomerEmail(),
                orderRequestDto.getProductId());

        Customer customer = customerRepository.findByEmailId(orderRequestDto.getCustomerEmail());
        if(customer==null){
            log.warn("Customer not found: {}",  orderRequestDto.getCustomerEmail());
            throw new CustomerNotFoundException("Customer Doesn't exisit");
        }

        log.info("Customer validated successfully: {}",  customer.getEmailId());

        Optional<Product> productOptional =
                productRepository.findById(orderRequestDto.getProductId());

        if(productOptional.isEmpty()){
            log.warn("Product not found with id: {}", orderRequestDto.getProductId());
            throw new ProductNotFoundException("Product doesn't exist");
        }

        Product product = productOptional.get();

        if(!product.isActive()){
            log.warn("Attempt to order deleted product with id: {}",
                    product.getId());

            throw new ProductNotFoundException(
                    "Product is no longer available");
        }

        log.info("Product validated successfully");

        Card card = cardRespository.findByCardNo(orderRequestDto.getCardNo());
        Date todayDate = new Date();

        if(card==null || card.getCvv()!=orderRequestDto.getCvv() || todayDate.after(card.getValidTill())){
            log.warn("Invalid card used by customer: {}", orderRequestDto.getCustomerEmail());
            throw new InvalidCardException("Invalid card");
        }

        log.info("Card validated successfully");


        if(!product.isActive()){
            log.warn("Attempt to order deleted product with id: {}",
                    product.getId());

            throw new ProductNotFoundException(
                    "Product is no longer available");
        }

        if(product.getAvailableQuantity() < orderRequestDto.getRequiredQuantity()){
            log.warn("Insufficient stock for product: {}", product.getProductName());
            throw new InsufficientQuantityException("Insufficient Quantity available");
        }

        log.info("Stock validation successful");

        int newQuantity = product.getAvailableQuantity()- orderRequestDto.getRequiredQuantity();
        product.setAvailableQuantity(newQuantity);
        if(newQuantity==0){
            product.setProductStatus(ProductStatus.OUT_OF_STOCK);
        }

        // prepare Order entity
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderId(String.valueOf(UUID.randomUUID()));
        orderEntity.setCardUsed(cardService.generateMaskedCard(orderRequestDto.getCardNo()));
        orderEntity.setOrderTotal(orderRequestDto.getRequiredQuantity()*product.getPrice());

        Item item = ItemTransformer.ItemRequestDtoToItem(orderRequestDto.getRequiredQuantity());
        item.setOrderEntity(orderEntity);
        item.setProduct(product);

        orderEntity.setCustomer(customer);
        orderEntity.getItems().add(item);

        OrderEntity savedOrder = orderEntityRepository.save(orderEntity);  // save order and item

        log.info("Order created successfully with orderId: {}",  savedOrder.getOrderId());

        product.getItems().add(savedOrder.getItems().get(0));
        customer.getOrders().add(savedOrder);

        // send email
        try{
            sendEmail(savedOrder);
        }
        catch(Exception e){
            log.error("Failed to send email for orderId: {}",  savedOrder.getOrderId(), e);
        }
        // in intellij console you can see the email failed statements --why not showing in response, json response doesnt show sop statements
//        catch(Exception e){
//            System.out.println("=================================");
//            System.out.println("EMAIL FAILED");
//            System.out.println(e.getMessage());
//            System.out.println("=================================");
//        }

        return OrderTransformer.OrderToOrderResponseDto(savedOrder);
    }

    @Transactional
    public OrderEntity placeOrder(Cart cart, Card card) {

        OrderEntity order = new OrderEntity();
        order.setOrderId(String.valueOf(UUID.randomUUID()));
        order.setCardUsed(cardService.generateMaskedCard(card.getCardNo()));

        int orderTotal = 0;
        for(Item item : cart.getItems()){

            Product product = item.getProduct();

            if(product.getAvailableQuantity() < item.getRequiredQuantity()){
                throw new InsufficientQuantityException(
                        "Sorry! Insufficient quantity available for: "
                                + product.getProductName());
            }

            int newQuantity =
                    product.getAvailableQuantity()
                            - item.getRequiredQuantity();

            product.setAvailableQuantity(newQuantity);

            if(newQuantity == 0){
                product.setProductStatus(ProductStatus.OUT_OF_STOCK);
            }

            orderTotal +=
                    product.getPrice() * item.getRequiredQuantity();
        }

        /* IMPORTANT */
        List<Item> orderItems = new ArrayList<>();

        for(Item cartItem : cart.getItems()){

            Item orderItem = new Item();

            orderItem.setRequiredQuantity(
                    cartItem.getRequiredQuantity());

            orderItem.setProduct(
                    cartItem.getProduct());

            orderItem.setOrderEntity(order);

            orderItems.add(orderItem);
        }

        order.setItems(orderItems);
        order.setOrderTotal(orderTotal);
        order.setCustomer(card.getCustomer());

        return order;
    }

    public void sendEmail(OrderEntity order){

        String text = "Congrats! Your order has been placed. Following are the details: '\n' " +
                "Order id = "+ order.getOrderId() + "\n"
                + "Order total = " + order.getOrderTotal()
                + "Order Date = " + order.getOrderDate();


        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(order.getCustomer().getEmailId());
        mail.setFrom("acciojobspring@gmail.com");
        mail.setSubject("Order Placed");
        mail.setText(text);


        javaMailSender.send(mail);
    }
}