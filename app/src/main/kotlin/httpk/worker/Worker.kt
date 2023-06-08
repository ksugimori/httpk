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


class Worker(private val handlerMethod: HttpHandler = DummyHttpHandler) {

    suspend fun execute(socket: Socket) {
        val request = socket.readHttpRequest()
        val response = handlerMethod(request)
        socket.writeHttpResponse(response)

        log("\"${request.requestLine}\" : ${response.status.code}")
    }

    private suspend fun Socket.readHttpRequest(): HttpRequest = withContext(Dispatchers.IO) {
        val inputStream = this@readHttpRequest.getInputStream()

        val requestLine = inputStream.readLine().let { RequestLine.parse(it) }

        val headers = HttpHeaders()
        inputStream.linesSequence()
            .takeWhile { it.isNotBlank() }
            .map { HttpHeaderParser.parse(it) }
            .forEach { (key, values) -> headers[key] = values }

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

    private suspend fun Socket.writeHttpResponse(response: HttpResponse) = withContext(Dispatchers.IO) {
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