package httpk.handler

import httpk.http.semantics.HttpHeaders
import httpk.http.semantics.HttpRequest
import httpk.http.semantics.HttpResponse
import httpk.http.semantics.HttpStatus

class DummyHttpHandler : HttpHandler {
    override fun handle(request: HttpRequest): HttpResponse {
        val body = """
                <!DOCTYPE html>
                <html>
                  <body>
                    Hello World!
                    Request: ${request.method} ${request.target}
                  </body>
                </html>
            """.trimIndent().toByteArray()

        val headers = HttpHeaders()
        headers.add("Content-Type", "text/html")
        headers.contentLength = body.size

        return HttpResponse(status = HttpStatus.OK, headers = headers, body = body)
    }
}