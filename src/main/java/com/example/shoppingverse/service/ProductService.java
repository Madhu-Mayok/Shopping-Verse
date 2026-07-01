package com.example.shoppingverse.service;

import com.example.shoppingverse.Enum.ProductCategory;
import com.example.shoppingverse.dto.request.ProductRequestDto;
import com.example.shoppingverse.dto.request.UpdateProductRequestDto;
import com.example.shoppingverse.dto.response.ProductResponseDto;
import com.example.shoppingverse.exception.ProductAlreadyDeletedException;
import com.example.shoppingverse.exception.ProductNotFoundException;
import com.example.shoppingverse.exception.SellerNotFoundException;
import com.example.shoppingverse.model.Product;
import com.example.shoppingverse.model.Seller;
import com.example.shoppingverse.repository.ProductRepository;
import com.example.shoppingverse.repository.SellerRepository;
import com.example.shoppingverse.transformer.ProductTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {


    private static final Logger log =   LoggerFactory.getLogger(ProductService.class);

    @Autowired
    SellerRepository sellerRepository;
    @Autowired
    private ProductRepository productRepository;

    //add product
    public ProductResponseDto addProduct(ProductRequestDto productRequestDto) {

        log.info("Adding product: {} for seller: {}",
                productRequestDto.getProductName(),
                productRequestDto.getSellerEmail());

        Seller seller = sellerRepository.findByEmailId(productRequestDto.getSellerEmail());

        if (seller == null) {
            log.warn("Seller not found with email: {}",
                    productRequestDto.getSellerEmail());

            throw new SellerNotFoundException("Seller doesn't exist");
        }

        //dto --> entity
        Product product = ProductTransformer.ProductRequestDtoToProduct(productRequestDto);
        product.setSeller(seller);
        seller.getProducts().add(product);

        Seller savedSeller = sellerRepository.save(seller);

        List<Product> productList = savedSeller.getProducts();
        Product latestProduct = productList.get(productList.size() - 1);

        log.info("Product added successfully. Product: {}, Seller: {}, ProductId: {}",
                latestProduct.getProductName(),
                seller.getName(),
                latestProduct.getId());

        // prepare response dto
        return ProductTransformer.ProductToProductResponseDto(latestProduct);
    }


    // Pagination -Pagination is the process of retrieving records in smaller chunks instead of loading the entire dataset at once.
    // Pageable - interface
    // PageRequest - implementation class of Pageable interface
    // PageRequest.of(0,5)  --Fetch first page containing 5 records.
    // findAll(pageable) loads only required records  and supports pagination/sorting.
    //What is Pageable?
    //Pageable is an interface used by Spring Data JPA to represent pagination information such as page number, page size, and sorting.
    //What is Page?
    //Page is a sublist of data returned from the database along with pagination metadata.

    // get products -- using pagination and sorting
//  public List<ProductResponseDto> getProducts(  int page, int size,  String sortBy){
//        Pageable pageable =   PageRequest.of(page,size, Sort.by(sortBy));
//        Page<Product> productPage =    productRepository.findAll(pageable);
//        List<ProductResponseDto> responseList =   new ArrayList<>();
//        for(Product product : productPage.getContent()){
//            responseList.add(
//                    ProductTransformer
//                            .ProductToProductResponseDto(product)
//            );
//        }
//        return responseList;
//    }

    // get products -- using pagination and sorting
    public Page<ProductResponseDto> getProducts(

            int pageNo,
            int pageSize,
            String sortBy,
            String direction){

        List<String> allowedFields = List.of(
                "productName",
                "price",
                "availableQuantity",
                "category"
        );

        if(!allowedFields.contains(sortBy)){
            throw new IllegalArgumentException(
                    "Allowed sort fields: productName, price, availableQuantity, category");
        }

        List<String> allowedDirections =   List.of("asc", "desc");

        if(!allowedDirections.contains(direction.toLowerCase())){
            throw new IllegalArgumentException(
                    "Allowed directions: asc, desc");
        }

        Sort sort =  direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable =
                PageRequest.of(
                        pageNo,
                        pageSize,
                        sort
                );

        Page<Product> products =
                productRepository.findByActiveTrue(pageable);

        return products.map(
                ProductTransformer::ProductToProductResponseDto
        );
    }

    //get all products by page no and size
    public Page<ProductResponseDto> getAllProducts(   int pageNo,  int pageSize){

        Pageable pageable =     PageRequest.of(pageNo,pageSize);

        Page<Product> products =   productRepository.findByActiveTrue(pageable);

        return products.map(    ProductTransformer::ProductToProductResponseDto   );
    }

    //get all products sorted
    public Page<ProductResponseDto> getAllProductsSorted(
            int pageNo,
            int pageSize,
            String sortBy){

        List<String> allowedFields = List.of(
                "productName",
                "price",
                "availableQuantity",
                "category"
        );

        if(!allowedFields.contains(sortBy)){
            throw new IllegalArgumentException(
                    "Allowed sort fields: productName, price, availableQuantity, category");
        }

        Pageable pageable = PageRequest.of(
                        pageNo,
                        pageSize,
                        Sort.by(sortBy)
                );



        Page<Product> products =
                productRepository.findByActiveTrue(pageable);

        return products.map(
                ProductTransformer::ProductToProductResponseDto
        );
    }

    // search product by name
    public List<ProductResponseDto> getProductsByName(String productName){

        List<Product> products =      productRepository.findByProductNameAndActiveTrue(productName);

        // Instead of returning an empty collection, I return a custom exception explaining why no data was found.
        if(products.isEmpty()){
            throw new ProductNotFoundException(
                    "No product found with name: " + productName);
        }
        List<ProductResponseDto> responseList =    new ArrayList<>();
        for(Product product : products){

            responseList.add(
                    ProductTransformer
                            .ProductToProductResponseDto(product)
            );
        }

        return responseList;
    }

    // search  product by category name
    public List<ProductResponseDto>
    getProductsByCategory(ProductCategory category){

        List<Product> products =    productRepository.findByCategoryAndActiveTrue(category);

        //Instead of returning an empty collection, I return a custom exception explaining why no data was found.
        if(products.isEmpty()){
            throw new ProductNotFoundException(
                    "No products available in category: " + category);
        }

        List<ProductResponseDto> responseList =     new ArrayList<>();

        for(Product product : products){

            responseList.add(
                    ProductTransformer
                            .ProductToProductResponseDto(product)
            );
        }

        return responseList;
    }

    // search product in range
    public List<ProductResponseDto> getProductsByPriceRange( int minPrice, int maxPrice){

        if(minPrice > maxPrice){
            throw new RuntimeException(    "minPrice cannot be greater than maxPrice");
        }

        List<Product> products = productRepository .findByPriceBetweenAndActiveTrue(  minPrice, maxPrice );

        //Instead of returning an empty collection, I return a custom exception explaining why no data was found.
        if(products.isEmpty()){
            throw new ProductNotFoundException(
                    "No products found in given price ");
        }
        List<ProductResponseDto> responseList =  new ArrayList<>();

        for(Product product : products){

            responseList.add( ProductTransformer .ProductToProductResponseDto(product) );
        }

        return responseList;
    }

    // get product where price greater than (mentioned)
    public List<ProductResponseDto> getProdByCategoryAndPriceGreaterThan(int price, ProductCategory category) {

        List<Product> products = productRepository.getProdByCategoryAndPriceGreaterThan(price,category);

        //Instead of returning an empty collection, I return a custom exception explaining why no data was found.
        if(products.isEmpty()){
            throw new ProductNotFoundException(
                    "No products found for category "
                            + category +
                            " above price " +
                            price);
        }

        // prepare list of response dto's
        List<ProductResponseDto> productResponseDtos = new ArrayList<>();
        for(Product product: products){
            productResponseDtos.add(ProductTransformer.ProductToProductResponseDto(product));
        }

        return productResponseDtos;

    }

    //update product
    public ProductResponseDto updateProduct(int productId, UpdateProductRequestDto dto) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new ProductNotFoundException("Product not found"));

        if(!product.isActive()){
            throw new ProductNotFoundException("Product not found");
        }

        product.setProductName(dto.getProductName());
        product.setPrice(dto.getPrice());
        product.setAvailableQuantity(dto.getAvailableQuantity());
        product.setCategory(dto.getCategory());

        Product savedProduct = productRepository.save(product);

        return ProductTransformer.ProductToProductResponseDto(savedProduct);
    }

    //patch product price
    public String updatePrice(int productId,int price){

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new ProductNotFoundException("Product not found"));

        // deleted products price won't be updated
        if(!product.isActive()){
            throw new ProductNotFoundException("Product not found");
        }

        product.setPrice(price);

        productRepository.save(product);

        return "Price updated successfully";
    }

    //delete product
    public String deleteProduct(int productId){

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new ProductNotFoundException("Product not found"));
    //  simply return message -easy
    //        if(!product.isActive()){
    //            return "Product already deleted";
    //        }

        if(!product.isActive()){
            throw new ProductAlreadyDeletedException("Product already deleted");
        }
        //soft delete -- change status and save product to DB
        product.setActive(false);
        productRepository.save(product);

        // hard delete -- deletes product from DB
        //productRepository.delete(product);

        return "Product deleted successfully";
    }

    //only deleted products
    public List<ProductResponseDto> getDeletedProducts(){

        List<Product> products =   productRepository.findByActiveFalse();

        List<ProductResponseDto> responseList =     new ArrayList<>();

        for(Product product : products){

            responseList.add(
                    ProductTransformer
                            .ProductToProductResponseDto(product)
            );
        }

        return responseList;
    }
}