package httpk.handler

import httpk.log
import httpk.util.getInputStreamSuspending
import httpk.util.getOutputStreamSuspending
import httpk.util.readLineSuspending
import java.io.PrintWriter
import java.net.Socket

class EchoHandler() : Handler {
    override suspend fun handle(socket: Socket) {
        log("connected from: ${socket.inetAddress}")

        socket.use {
            val reader = it.getInputStreamSuspending().bufferedReader()
            val writer = PrintWriter(it.getOutputStreamSuspending())

            do {
                val line = reader.readLineSuspending()
                log("got \"$line\"")
                writer.println(line)
                writer.flush()

                if (line.isBlank()) break
            } while (true)
        }

        log("close connection: ${socket.inetAddress}")
    }

}