package httpk

import httpk.handler.DummyHttpHandler
import httpk.handler.HttpHandler
import httpk.log
import httpk.core.io.httpReader
import httpk.core.io.httpWriter
import java.net.Socket


class Worker(private val httpHandler: HttpHandler = DummyHttpHandler()) {

    fun execute(socket: Socket) {
        val request = socket.getInputStream().httpReader().readRequest()
        val response = httpHandler.handle(request)
        socket.getOutputStream().httpWriter().writeResponse(response)

        log("\"${request.requestLine}\" : ${response.status.code}")
    }

}