package com.example.shoppingverse.repository;

import com.example.shoppingverse.Enum.ProductCategory;
import com.example.shoppingverse.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product,Integer> {


    List<Product> findByProductNameAndActiveTrue(String productName);

    List<Product> findByCategoryAndActiveTrue(ProductCategory category);

    List<Product> findByPriceBetweenAndActiveTrue(  int minPrice, int maxPrice );

    Page<Product> findByActiveTrue(Pageable pageable);

    List<Product> findByActiveFalse();

    //To write custom query -- use @Query
    @Query("select p from Product p where p.category=:category and p.price>:price and p.active=true")
    List<Product> getProdByCategoryAndPriceGreaterThan( @Param("price") int price,  @Param("category") ProductCategory category);
    //@Param("price") -- becoz i give requestparam names as price & category otherwise we can write like
    //List<Product> getProdByCategoryAndPriceGreaterThan(int price, ProductCategory category);
}
