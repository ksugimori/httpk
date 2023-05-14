package httpk.handler

import httpk.core.io.HttpRequestReader
import httpk.core.message.*
import httpk.log
import httpk.util.getInputStreamSuspending
import httpk.util.getBufferedWriterSuspending
import java.io.PrintWriter
import java.net.Socket


class HttpHandler() : Handler {

    override suspend fun handle(socket: Socket) {
        socket.use {
            val reader = HttpRequestReader(it.getInputStreamSuspending())
            val writer = PrintWriter(it.getBufferedWriterSuspending()) // TODO HttpResponseWriter 作ろう

            val requestLine = reader.readRequestLine()
            val headers = reader.readHeaders()
            val body = reader.readBody()

            val request = HttpRequest(
                method = requestLine.method,
                path = requestLine.path,
                version = requestLine.version,
                headers = headers,
                body = body
            )
            log("Request: $request")

            // TODO ドキュメント取得
            // TODO HttpResponseWriter に移動

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

            writer.println("${response.version} ${response.status.code} ${response.status.message}")
            response.headers.forEach { item ->
                writer.println("${item.key}: ${item.values.joinToString(", ")}")
            }
            writer.println()
            writer.println(responseBody)
            writer.flush()
        }

        log("close connection: ${socket.inetAddress}")
    }

}