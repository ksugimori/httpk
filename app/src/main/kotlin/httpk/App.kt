/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package httpk

import httpk.handler.StaticResourceHandler
import java.net.ServerSocket
import java.nio.file.Path
import java.nio.file.Paths
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun log(message: String) {
    val dateTimeFormatter = DateTimeFormatter
        .ofPattern("uuuu-MM-dd HH:mm:ss.SSS")
        .withZone(ZoneId.of("Asia/Tokyo"))
    val timestamp = dateTimeFormatter.format(Instant.now())
    println("[${timestamp}] [${Thread.currentThread().name}] $message")
}

class App(private val documentRoot: Path) {

    fun listen(port: Int) {
        val serverSocket = ServerSocket(port)
        log("server start. waiting on port $port")

        serverSocket.use {
            while (true) {
                val socket = it.accept()
                val worker = Worker(StaticResourceHandler(documentRoot))
                socket.use(worker::execute)
            }
        }
    }

}

fun main() {
    Runtime.getRuntime().addShutdownHook(Thread { log("server terminated.") })

    val documentRoot = Paths.get("../sample").toAbsolutePath().normalize()
    App(documentRoot).listen(8080)
}
