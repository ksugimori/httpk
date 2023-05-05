package httpk.handler

import httpk.core.RequestLine
import httpk.core.parseRequestLine
import httpk.log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.PrintWriter
import java.net.Socket

// 読み込み状態。TODO 状態数、遷移を整理する
enum class Status {
    INIT, HEADER, BODY, END
}

class HttpHandler() : Handler {
    override suspend fun handle(socket: Socket) {
        var status = Status.INIT

        socket.use {
            val reader = getBufferedReaderSuspend(it)
            val writer = PrintWriter(getBufferedWriterSuspend(it))

            var hasBody = false
            var requestLine: RequestLine? = null
            val headers = mutableListOf<String>()
            while (status != Status.END) {
                val line = readLineSuspend(reader)

                // TODO state パターンにできる？
                when (status) {
                    Status.INIT -> {
                        requestLine = parseRequestLine(line)
                        status = Status.HEADER
                    }

                    Status.HEADER -> {
                        headers.add(line)
                        if (line.isBlank()) status = Status.END
                    }

                    else -> {
                        status = Status.END
                    }

                }

            }

            log("Request: $requestLine")

            // TODO ドキュメント取得
            // TODO Response クラス作成
            writer.println("${requestLine?.version} 200 OK")
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