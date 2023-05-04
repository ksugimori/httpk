/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package httpk

import httpk.handler.EchoHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.net.ServerSocket

// TODO パラメーターにする
private const val DEFAULT_SERVER_PORT = 8080

fun log(message: String) = println("[LOG] [${Thread.currentThread().name}] $message")

class App {
    private val port = DEFAULT_SERVER_PORT
    private val echoHandler: EchoHandler = EchoHandler()
    fun execute() = runBlocking(Dispatchers.IO) {
        val serverSocket = ServerSocket(port)
        serverSocket.use { server ->
            log("server start. waiting on port ${server.localPort}")

            while (true) {
                val client = server.accept()
                launch { client.use(echoHandler::handle) }
            }
        }
    }

}

fun main() {
    System.setProperty("kotlinx.coroutines.debug", "on")

    App().execute()
}
