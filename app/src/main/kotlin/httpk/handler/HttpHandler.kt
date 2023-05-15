package httpk.handler

import httpk.core.io.HttpRequestReader
import httpk.core.io.HttpResponseWriter
import httpk.core.message.*
import httpk.log
import httpk.util.getInputStreamSuspending
import httpk.util.getOutputStreamSuspending
import java.net.Socket


class HttpHandler() : Handler {

    override suspend fun handle(socket: Socket) {
        socket.use {
            val reader = HttpRequestReader(it.getInputStreamSuspending())
            val writer = HttpResponseWriter(it.getOutputStreamSuspending())

            val request = reader.readRequest()

            // TODO ドキュメント取得
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
            val response = HttpResponse(
                version = HttpVersion.HTTP_1_1,
                status = HttpStatus.OK,
                headers = responseHeaders,
                body = responseBody
            )

            writer.writeResponse(response)

            log("\"${request.requestLine}\" : ${response.status.code}")
        }

    }

}