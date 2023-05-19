package httpk.handler

import httpk.core.message.*

class DummyHttpHandler : HttpHandler {
    override fun handle(request: HttpRequest): HttpResponse {
        val responseBody = """
                <!DOCTYPE html>
                <html>
                  <body>
                    Hello World!
                    Request: ${request.method} ${request.path}
                  </body>
                </html>
            """.trimIndent()

        val responseHeaders = HttpHeaders()
        responseHeaders["Content-Type"] = "text/html"
        responseHeaders["Content-Length"] = responseBody.toByteArray().size

        return HttpResponse(
            version = HttpVersion.HTTP_1_1,
            status = HttpStatus.OK,
            headers = responseHeaders,
            body = responseBody
        )
    }
}