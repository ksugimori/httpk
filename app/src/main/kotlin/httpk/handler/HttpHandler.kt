package httpk.handler

import httpk.core.HttpRequest
import httpk.core.RequestLine
import httpk.core.parseRequestLine
import httpk.log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.PrintWriter
import java.net.Socket

private class RequestBuilder() {
    var requestLine: RequestLine? = null
    var headers: MutableList<String> = mutableListOf()

    fun build(): HttpRequest {
        if (requestLine == null) throw IllegalStateException("requestLine not initialized")

        return HttpRequest(
            method = requestLine!!.method,
            path = requestLine!!.path,
            version = requestLine!!.version,
            headers = headers,
            body = null
        )
    }
}

private sealed class State(val value: String) {
    // TODO State の中に builder を持つと static にせざるを得ないので気持ち悪い
    companion object {
        val builder: RequestBuilder = RequestBuilder()
    }
    abstract fun process(line: String): State
    override fun toString(): String = value

    object Init : State("INIT") {
        override fun process(line: String): State {
            builder.requestLine = parseRequestLine(line)
            return State.Header
        }
    }

    object Header : State("HEADER") {
        override fun process(line: String): State {
            return if (line.isBlank()) {
                State.End
            } else {
                builder.headers.add(line)
                State.Header
            }
        }
    }

    object End : State("END") {
        override fun process(line: String) = State.End
    }
}

class HttpHandler() : Handler {

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

            val request = State.builder.build()
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