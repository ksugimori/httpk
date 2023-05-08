/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package httpk

import httpk.handler.EchoHandler
import httpk.handler.HttpHandler
import kotlinx.coroutines.*
import java.net.ServerSocket

fun log(message: String) = println("[LOG] [${Thread.currentThread().name}] $message")

class App() {

    fun listen(port: Int) {
        val serverSocket = ServerSocket(port)
        log("server start. waiting on port $port")

        serverSocket.use { dispatchRequests(it) }
    }

    private fun dispatchRequests(serverSocket: ServerSocket) = runBlocking {
        while (true) {
            val socket = withContext(Dispatchers.IO) { serverSocket.accept() }
//            launch { EchoHandler().handle(socket) }
            launch { HttpHandler().handle(socket) }
        }


    }

}

fun main() {
    Runtime.getRuntime().addShutdownHook(Thread { log("server terminated.") })
    System.setProperty("kotlinx.coroutines.debug", "on")

    App().listen(8080)
}
