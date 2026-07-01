package com.example.shoppingverse.controller;

import com.example.shoppingverse.Enum.ProductCategory;
import com.example.shoppingverse.dto.request.ProductPriceUpdateDto;
import com.example.shoppingverse.dto.request.ProductRequestDto;
import com.example.shoppingverse.dto.request.UpdateProductRequestDto;
import com.example.shoppingverse.dto.response.ProductResponseDto;
import com.example.shoppingverse.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
//Without @Validated, request param validation won't work.
@RestController
@RequestMapping("/product")
@Tag(
        name = "Product APIs",
        description = "Product management operations"
)
public class ProductController {

    @Autowired
    ProductService productService;

    @Operation(
            summary = "add  product",
            description = "Adds/creates a new product"
    )
    @PreAuthorize("hasRole('SELLER')")
    @PostMapping("/add")
    public ResponseEntity<ProductResponseDto> addProduct( @Valid @RequestBody ProductRequestDto dto){

        ProductResponseDto response =
                productService.addProduct(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @Operation(
            summary = "Get products using filter",
            description = "Returns all products of mentioned page ,size ,filter and sorting "
    )
    @GetMapping("/all/filter")
    public ResponseEntity<Page<ProductResponseDto>> getProducts(

            @RequestParam(defaultValue = "0") @Min(0)
            int pageNo,

            @RequestParam(defaultValue = "5") @Min(1)
            int pageSize,

            @RequestParam(defaultValue = "price")
            String sortBy,

            @RequestParam(defaultValue = "asc")
            String direction){

        return ResponseEntity.ok(
                productService.getProducts(
                        pageNo,
                        pageSize,
                        sortBy,
                        direction)
        );
    }

    @Operation(
            summary = "Get products by default page and size mentioned",
            description = "Returns all products of mentioned page ,size"
    )
    @GetMapping("/all/Default")
    public ResponseEntity<Page<ProductResponseDto>>    getAllProducts(
            @RequestParam(defaultValue = "0") @Min(0)int pageNo,
            @RequestParam(defaultValue = "5") @Min(1)int pageSize){

        return ResponseEntity.ok(
                productService.getAllProducts(
                        pageNo,
                        pageSize)
        );
    }

    @Operation(
            summary = "Get products by default page no ,size and sorting[price] ",
            description = "Returns all products of mentioned page ,size and sorting"
    )
    @GetMapping("/all/sorted")
    public ResponseEntity<Page<ProductResponseDto>>  getAllProductsSorted(

            @RequestParam(defaultValue = "0") @Min(0)
            int pageNo,

            @RequestParam(defaultValue = "5") @Min(1)
            int pageSize,

            @RequestParam(defaultValue = "price")
            String sortBy){

        return ResponseEntity.ok(
                productService.getAllProductsSorted(
                        pageNo,
                        pageSize,
                        sortBy)
        );
    }

    @Operation(
            summary = "Search products by category and price",
            description = "Returns all products belonging to a category and price > than mentioned"
    )
    @GetMapping("/search/get_by_category_and_price_greater_than")
    public ResponseEntity getProdByCategoryAndPriceGreaterThan(@RequestParam("price") @Min(0) int price,
                                                               @RequestParam("category")ProductCategory category){

        List<ProductResponseDto> productResponseDtoList =
                productService.getProdByCategoryAndPriceGreaterThan(price,category);
        return new ResponseEntity(productResponseDtoList,HttpStatus.FOUND);
    }

    @Operation(
            summary = "Update product",
            description = "updates existing or replaces old product with new product"
    )
    @PreAuthorize("hasRole('SELLER')")
    @PutMapping("/update/{productId}")
    public ResponseEntity<?> updateProduct( @PathVariable int productId, @Valid @RequestBody UpdateProductRequestDto dto){

        ProductResponseDto response =
                productService.updateProduct(productId,dto);

        return new ResponseEntity<>(response,HttpStatus.OK);
    }


    @Operation(
            summary = "Search products by name",
            description = "Returns all products matched"
    )
    @GetMapping("/search/name")
    public ResponseEntity<List<ProductResponseDto>>  getProductsByName(  @RequestParam String productName){

        return ResponseEntity.ok(
                productService.getProductsByName(productName)
        );
    }

    @Operation(
            summary = "Search products by category",
            description = "Returns all products belonging to a category"
    )
    @GetMapping("/search/category")
    public ResponseEntity<List<ProductResponseDto>>
    getProductsByCategory(
            @RequestParam ProductCategory category){

        return ResponseEntity.ok(
                productService.getProductsByCategory(category)
        );
    }

    @Operation(
            summary = "Search products using price-range",
            description = "Returns all products belonging to the price-range"
    )
    @GetMapping("/search/price-range")
    public ResponseEntity<List<ProductResponseDto>>    getProductsByPriceRange(

            @RequestParam @Min(0) int minPrice,

            @RequestParam @Min(0) int maxPrice){

        return ResponseEntity.ok(
                productService.getProductsByPriceRange(
                        minPrice,maxPrice));
    }

    @Operation(
            summary = "Deletes product by productId",
            description = "Deletes product matching productId"
    )
    @PreAuthorize("hasRole('SELLER')")
    @DeleteMapping("/delete/{productId}")
    public ResponseEntity<String> deleteProduct(   @PathVariable int productId){

//        String response = productService.deleteProduct(productId);
//        return ResponseEntity.ok(response);

        return ResponseEntity.ok( productService.deleteProduct(productId));
    }


    @Operation(
            summary = "Price Update",
            description = "Updates product price"
    )
    @PreAuthorize("hasRole('SELLER')")
    @PatchMapping("/price/{productId}")
    public ResponseEntity<String> updatePrice(  @PathVariable int productId,    @Valid @RequestBody ProductPriceUpdateDto dto){

        return ResponseEntity.ok(
                productService.updatePrice(productId, dto.getPrice())
        );
    }

    //soft delete feature -- after deleting product , database rows still exists for history,auditing,reporting
    @Operation(
            summary = "Deleted Products",
            description = "Gives deleted products list"
    )
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/deleted")
    public ResponseEntity<List<ProductResponseDto>> getDeletedProducts(){

        return ResponseEntity.ok(
                productService.getDeletedProducts()
        );
    }
}