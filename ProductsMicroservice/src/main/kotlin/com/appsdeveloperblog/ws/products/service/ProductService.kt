package com.appsdeveloperblog.ws.products.service

import com.appsdeveloperblog.ws.products.rest.CreateProductRestModel
import kotlin.jvm.Throws

interface ProductService {

    @Throws(Exception::class)
    fun createProduct(productRestModel: CreateProductRestModel): String
}