package com.appsdeveloperblog.ws.products.rest

import com.appsdeveloperblog.ws.products.service.ProductService
import com.appsdeveloperblog.ws.products.service.ProductServiceImpl
import com.appsdeveloperblog.ws.products.service.ProductServiceImpl.Companion
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/products")
class ProductController(val productService: ProductService) {

    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(this::class.java)
    }

    @PostMapping
    fun createProduct(@RequestBody product: CreateProductRestModel): ResponseEntity<Any> {

        var productId: String?  = null
        try {
            productId = productService.createProduct(product)
        } catch (e: Exception){
           //e.printStackTrace()
           LOGGER.info(String.format("****** Returning product with id: %s", productId))
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorMessage(Date(), e.message,"/products"))
        }



        return ResponseEntity.status(HttpStatus.CREATED).body(productId)
    }
}