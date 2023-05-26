package httpk.worker

import httpk.core.message.*
import httpk.handler.DummyHttpHandler
import httpk.handler.HttpHandler
import httpk.log
import httpk.util.linesSequence
import httpk.util.readLine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.PrintWriter
import java.net.Socket


class Worker(private val handler: HttpHandler = DummyHttpHandler()) {

    suspend fun execute(socket: Socket) {
        val request = socket.readHttpRequest()
        val response = handler.handle(request)
        socket.writeHttpResponse(response)

        log("\"${request.requestLine}\" : ${response.status.code}")
    }

    private suspend fun Socket.readHttpRequest(): HttpRequest {
        val inputStream = this.getInputStream()

        return withContext(Dispatchers.IO) {

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

    private suspend fun Socket.writeHttpResponse(response: HttpResponse) {
        val writer = PrintWriter(this.getOutputStream())

        withContext(Dispatchers.IO) {
            writer.print("${response.statusLine}$CRLF")
            response.headers.forEach { item ->
                writer.print("$item$CRLF")
            }
            writer.print(CRLF)
            writer.print(response.body)

            writer.flush()
        }
    }

    companion object {
        private const val CRLF = "\r\n"
    }
}