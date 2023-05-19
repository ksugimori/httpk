package httpk.handler

import httpk.core.message.HttpRequest
import httpk.core.message.HttpResponse

interface HttpHandler {
    fun handle(request: HttpRequest): HttpResponse
}