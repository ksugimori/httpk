package httpk.server

import httpk.handler.DefaultRouter
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
                val worker = Worker(DefaultRouter(documentRoot))
                socket.use(worker::execute)
            }
        }
    }

}