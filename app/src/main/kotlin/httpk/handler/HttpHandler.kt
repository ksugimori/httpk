package httpk.handler

import httpk.core.io.HttpResponseWriter
import httpk.core.message.*
import httpk.log
import httpk.util.getOutputStreamSuspending
import httpk.util.linesSequence
import httpk.util.readLine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.Socket


class HttpHandler() : Handler {

    override suspend fun handle(socket: Socket) {
        socket.use {
            val writer = HttpResponseWriter(it.getOutputStreamSuspending())

            val request = readHttpRequest(it)

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

    private suspend fun readHttpRequest(socket: Socket): HttpRequest = withContext(Dispatchers.IO) {
        val inputStream = socket.getInputStream()

        val requestLine = inputStream.readLine().let { RequestLine.parse(it) }

        val headers = HttpHeaders()
        inputStream.linesSequence()
            .takeWhile { it.isNotBlank() }
            .map { HttpHeaderItem.parse(it) }
            .forEach { headers.add(it) }

        val body = if (headers.contentLength > 0) {
            String(inputStream.readNBytes(headers.contentLength))
        } else {
            null
        }

        HttpRequest(
            method = requestLine.method,
            path = requestLine.path,
            version = requestLine.version,
            headers = headers,
            body = body
        )
    }

}