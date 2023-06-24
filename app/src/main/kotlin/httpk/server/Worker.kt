package httpk.server

import httpk.handler.DummyHttpHandler
import httpk.handler.HttpHandler
import httpk.util.consoleLog
import httpk.util.httpReader
import httpk.util.httpWriter
import java.net.Socket


class Worker(private val httpHandler: HttpHandler = DummyHttpHandler()) {

    fun execute(socket: Socket) {
        val request = socket.getInputStream().httpReader().readRequest()
        val response = httpHandler.handle(request)
        socket.getOutputStream().httpWriter().writeResponse(response)

        consoleLog("\"${request.method} ${request.target} ${request.version}\" : ${response.status.code}")
    }

}