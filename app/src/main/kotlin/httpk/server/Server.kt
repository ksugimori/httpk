package httpk.server

import httpk.handler.Router
import httpk.handler.StaticResourceHandler
import httpk.util.consoleLog
import java.net.ServerSocket
import java.nio.file.Path

class Server(private val documentRoot: Path, private val port: Int) {
    fun listen() {
        val serverSocket = ServerSocket(port)
        consoleLog("server start. waiting on port $port")

        serverSocket.use {
            while (true) {
                val socket = it.accept()
                val worker = Worker(StaticResourceHandler(documentRoot), Router(documentRoot))
                socket.use(worker::execute)
            }
        }
    }

}