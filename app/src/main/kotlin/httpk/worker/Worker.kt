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


class Worker(private val socket: Socket) {

    suspend fun execute() {
        val request = readHttpRequest()

        val handler: HttpHandler = DummyHttpHandler()
        val response = handler.handle(request)

        writeHttpResponse(response)

        log("\"${request.requestLine}\" : ${response.status.code}")
    }

    private suspend fun readHttpRequest(): HttpRequest = withContext(Dispatchers.IO) {
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

    private suspend fun writeHttpResponse(response: HttpResponse) = withContext(Dispatchers.IO) {
        val writer = PrintWriter(socket.getOutputStream())

        writer.print("${response.statusLine}$CRLF")
        response.headers.forEach { item ->
            writer.print("$item$CRLF")
        }
        writer.print(CRLF)
        writer.print(response.body)

        writer.flush()
    }

    companion object {
        private const val CRLF = "\r\n"
    }
}