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
        responseHeaders.add("Content-Type", "text/html")
        responseHeaders.contentLength = responseBody.toByteArray().size

        return HttpResponse(
            version = HttpVersion.HTTP_1_1,
            status = HttpStatus.OK,
            headers = responseHeaders,
            body = responseBody
        )
    }
}