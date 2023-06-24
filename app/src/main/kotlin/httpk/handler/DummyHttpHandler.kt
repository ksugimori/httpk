package httpk.handler

import httpk.http.semantics.*

class DummyHttpHandler : HttpHandler {
    override fun handle(request: HttpRequest): HttpResponse {
        val responseBody = """
                <!DOCTYPE html>
                <html>
                  <body>
                    Hello World!
                    Request: ${request.method} ${request.target}
                  </body>
                </html>
            """.trimIndent().toByteArray()

        val responseHeaders = HttpHeaders()
        responseHeaders.add("Content-Type", "text/html")
        responseHeaders.contentLength = responseBody.size

        return HttpResponse(
            version = HttpVersion.HTTP_1_1,
            status = HttpStatus.OK,
            headers = responseHeaders,
            body = responseBody
        )
    }
}