package com.appsdeveloperblog.ws.products.rest

import java.util.*

class ErrorMessage {
    var timeStamp: Date? = null
    var message: String? = null
    var details: String? = null

    constructor() {}

    constructor(timeStamp: Date?, message: String?, details: String?) {
        this.timeStamp = timeStamp
        this.message = message
        this.details = details
    }
}