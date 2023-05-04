package httpk.handler

import httpk.log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.PrintWriter
import java.net.Socket

class EchoHandler() : Handler {
    override suspend fun handle(socket: Socket) {
        log("connected from: ${socket.inetAddress}")

        socket.use {
            val reader = getBufferedReaderSuspend(it)
            val writer = PrintWriter(getBufferedWriterSuspend(it))

            do {
                val line = readLineSuspend(reader)
                log("got \"$line\"")
                writer.println(line)
                writer.flush()

                if (line.isBlank()) break
            } while (true)
        }

        log("close connection: ${socket.inetAddress}")
    }

    //
    // private
    //
    private suspend fun getBufferedReaderSuspend(socket: Socket): BufferedReader {
        return withContext(Dispatchers.IO) { socket.getInputStream() }.bufferedReader()
    }

    private suspend fun getBufferedWriterSuspend(socket: Socket): BufferedWriter {
        return withContext(Dispatchers.IO) { socket.getOutputStream() }.bufferedWriter()
    }

    private suspend fun readLineSuspend(reader: BufferedReader): String {
        return withContext(Dispatchers.IO) { reader.readLine() }
    }

}