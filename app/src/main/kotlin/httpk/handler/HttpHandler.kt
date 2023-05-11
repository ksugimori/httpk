package httpk.handler

import httpk.core.HttpRequestParser
import httpk.log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.PrintWriter
import java.net.Socket


class HttpHandler() : Handler {

    override suspend fun handle(socket: Socket) {
        socket.use {
            val reader = getBufferedReaderSuspend(it)
            val writer = PrintWriter(getBufferedWriterSuspend(it))

            val parser = HttpRequestParser()

            while (parser.isNotCompleted()) {
                val line = readLineSuspend(reader)
                parser.read(line)
            }

            val request = parser.build()
            log("Request: $request")

            // TODO ドキュメント取得
            // TODO Response クラス作成
            writer.println("HTTP/1.1 200 OK")
            writer.flush()
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