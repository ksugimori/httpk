package httpk.server

import httpk.exception.InvalidHttpMessageException
import httpk.handler.DummyHttpHandler
import httpk.handler.HttpHandler
import httpk.http.io.HttpReader
import httpk.http.io.HttpWriter
import httpk.http.semantics.HttpRequest
import httpk.http.semantics.HttpResponse
import httpk.http.semantics.HttpStatus
import httpk.util.consoleLog
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket

private fun InputStream.httpReader() = HttpReader(this)
private fun OutputStream.httpWriter() = HttpWriter(this)

class Worker(private val httpHandler: HttpHandler = DummyHttpHandler()) {

    fun execute(socket: Socket) {
        val httpReader = socket.getInputStream().httpReader()
        val httpWriter = socket.getOutputStream().httpWriter()

        var willKeepAlive = true
        do {
            val request = try {
                httpReader.readRequest()
            } catch (ex: InvalidHttpMessageException) {
                httpWriter.writeResponse(HttpResponse(status = HttpStatus.BAD_REQUEST))
                consoleLog("\"cannot parse request\" : 400 : ${ex.message}")
                willKeepAlive = true
                continue
            }

            val response = httpHandler.handle(request)
            httpWriter.writeResponse(response)

            if (request.headers["Connection"]?.contains("Close") == true) {
                willKeepAlive = false
            }

            accessLog(request, response)
        } while (willKeepAlive)
    }

    private fun accessLog(request: HttpRequest, response: HttpResponse) {
        consoleLog("\"${request.method} ${request.target} ${request.version}\" : ${response.status.code}")
    }

}