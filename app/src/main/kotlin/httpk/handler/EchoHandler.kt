package httpk.handler

import httpk.log
import java.io.PrintWriter
import java.net.Socket

class EchoHandler : Handler {
    override fun handle(socket: Socket) {
        log("connected from ${socket.inetAddress}")

        val reader = socket.getInputStream().bufferedReader()
        val writer = PrintWriter(socket.getOutputStream().bufferedWriter())

        do {
            val line = reader.readLine()
            log("got \"$line\"")
            writer.println(line)
            writer.flush()

            if (line.isBlank()) break
        } while (true)
    }

}