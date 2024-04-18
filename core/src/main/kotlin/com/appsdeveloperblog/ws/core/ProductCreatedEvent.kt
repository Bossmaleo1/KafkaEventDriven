package com.appsdeveloperblog.ws.core

class ProductCreatedEvent {
    var productId: String? = null
    var title: String? = null
    var price: String? = null
    var quantity: Int? = null

    constructor(){}

    constructor(productId: String, title: String, price: String, quantity: Int){
        this.productId = productId
        this.title = title
        this.price = price
        this.quantity = quantity
    }
}