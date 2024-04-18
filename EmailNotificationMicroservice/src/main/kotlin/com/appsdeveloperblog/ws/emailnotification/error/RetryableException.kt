package com.appsdeveloperblog.ws.emailnotification.error

class RetryableException: RuntimeException {
    constructor(cause: Throwable?) : super(cause)
    constructor(message: String?) : super(message)

}