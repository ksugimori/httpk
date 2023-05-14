package httpk.handler

import httpk.log
import httpk.util.getBufferedReaderSuspending
import httpk.util.getBufferedWriterSuspending
import httpk.util.readLineSuspending
import java.io.PrintWriter
import java.net.Socket

class EchoHandler() : Handler {
    override suspend fun handle(socket: Socket) {
        log("connected from: ${socket.inetAddress}")

        socket.use {
            val reader = it.getBufferedReaderSuspending()
            val writer = PrintWriter(it.getBufferedWriterSuspending())

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