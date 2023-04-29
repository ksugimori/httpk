/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package httpk

import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket

// TODO パラメーターにする
private const val DEFAULT_SERVER_PORT = 8080

fun log(message: String) = println("[LOG]  $message")

class App {
    fun execute() {
        val port = DEFAULT_SERVER_PORT
        ServerSocket(port).use { server ->
            log("server start. waiting on port $port")

            server.accept().use { socket ->
                log("connected from ${socket.inetAddress}")

                handle(socket)
            }
        }

        log("server terminated.")
    }

    private fun handle(socket: Socket) {
        val reader = socket.getInputStream().bufferedReader()
        val writer = PrintWriter(socket.getOutputStream().bufferedWriter())

        do {
            val line = reader.readLine()
            log("""got "$line"""")
            writer.println(line)
            writer.flush()

            if (line.isBlank()) break
        } while (true)

    }
}

fun main() {
    App().execute()
}
