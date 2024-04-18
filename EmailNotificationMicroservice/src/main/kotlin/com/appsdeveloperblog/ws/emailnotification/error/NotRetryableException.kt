package com.appsdeveloperblog.ws.emailnotification.error

class NotRetryableException: RuntimeException {
    constructor(cause: Throwable?) : super(cause)
    constructor(message: String?) : super(message)
}