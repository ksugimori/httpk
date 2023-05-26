package httpk.handler

import httpk.core.message.*

val DummyHttpHandler: HttpHandler = { request ->
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

    HttpResponse(
        version = HttpVersion.HTTP_1_1,
        status = HttpStatus.OK,
        headers = responseHeaders,
        body = responseBody
    )
}