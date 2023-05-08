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

class HttpHandler() : Handler {

    private sealed class State(val value: String) {
        abstract fun process(line: String): State
        override fun toString(): String = value

        object Init : State("INIT") {
            override fun process(line: String): State {
                val requestLine = parseRequestLine(line)
                log("Request: $requestLine")

                return State.Header
            }
        }

        object Header : State("HEADER") {
            override fun process(line: String): State {
                if (line.isBlank()) return State.End
                return State.Header
            }
        }

        object End : State("END") {
            override fun process(line: String): State = State.End
        }
    }

    override suspend fun handle(socket: Socket) {
        var state: State = State.Init

        socket.use {
            val reader = getBufferedReaderSuspend(it)
            val writer = PrintWriter(getBufferedWriterSuspend(it))

            while (state != State.End) {
                val line = readLineSuspend(reader)
                // TODO リクエストボディのビルダーを引数に（もしくはビルダー内に state を持たせる）
                state = state.process(line)
            }

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