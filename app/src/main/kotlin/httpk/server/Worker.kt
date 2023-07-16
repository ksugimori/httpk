package httpk.server

import httpk.handler.DummyHttpHandler
import httpk.handler.HttpHandler
import httpk.http.io.HttpReader
import httpk.http.io.HttpWriter
import httpk.util.consoleLog
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket

private fun InputStream.httpReader() = HttpReader(this)
private fun OutputStream.httpWriter() = HttpWriter(this)

class Worker(private val httpHandler: HttpHandler = DummyHttpHandler()) {

    fun execute(socket: Socket) {
        val request = socket.getInputStream().httpReader().readRequest()
        val response = httpHandler.handle(request)
        socket.getOutputStream().httpWriter().writeResponse(response)

        consoleLog("\"${request.method} ${request.target} ${request.version}\" : ${response.status.code}")
    }

}