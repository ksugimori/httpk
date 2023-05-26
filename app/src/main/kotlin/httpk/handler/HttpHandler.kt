package httpk.handler

import httpk.core.message.HttpRequest
import httpk.core.message.HttpResponse

typealias HttpHandler = (request: HttpRequest) -> HttpResponse
