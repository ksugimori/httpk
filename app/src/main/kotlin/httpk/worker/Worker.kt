package httpk.worker

import httpk.core.message.*
import httpk.handler.DummyHttpHandler
import httpk.handler.HttpHandler
import httpk.log
import httpk.util.linesSequence
import httpk.util.readLine
import java.io.PrintWriter
import java.net.Socket


class Worker(private val httpHandler: HttpHandler = DummyHttpHandler()) {

    fun execute(socket: Socket) {
        val request = socket.readHttpRequest()
        val response = httpHandler.handle(request)
        socket.writeHttpResponse(response)

        log("\"${request.requestLine}\" : ${response.status.code}")
    }

    private fun Socket.readHttpRequest(): HttpRequest {
        val inputStream = this.getInputStream()

        val requestLine = inputStream.readLine().let { RequestLine.parse(it) }

        val headers = HttpHeaders()
        inputStream.linesSequence()
            .takeWhile { it.isNotBlank() }
            .map { HttpHeaderParser.parse(it) }
            .forEach { (name, values) -> headers.addAll(name, values) }

        val body = if (headers.contentLength > 0) {
            String(inputStream.readNBytes(headers.contentLength))
        } else {
            null
        }

        return HttpRequest(
            method = requestLine.method,
            path = requestLine.path,
            version = requestLine.version,
            headers = headers,
            body = body
        )
    }

    private fun Socket.writeHttpResponse(response: HttpResponse) {
        val writer = PrintWriter(this@writeHttpResponse.getOutputStream())

        writer.print("${response.statusLine}$CRLF")
        response.headers.forEach { (key, values) ->
            writer.print("$key: ${values.joinToString(", ")}$CRLF")
        }
        writer.print(CRLF)
        writer.print(response.body)

        writer.flush()
    }

    companion object {
        private const val CRLF = "\r\n"
    }
}