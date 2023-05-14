package httpk.core.io

import httpk.core.message.RequestLine
import httpk.util.readLineSuspending
import java.io.BufferedReader
import java.io.Closeable

class HttpRequestReader(private val bufferedReader: BufferedReader) : Closeable by bufferedReader {
    private var state: State = State.RequestLine
    private var hasBody: Boolean = false

    suspend fun readRequestLine(): RequestLine {
        require(state == State.RequestLine)

        val line = bufferedReader.readLineSuspending()
        val requestLine = RequestLine.parse(line)

        state = State.Header

        return requestLine
    }

    suspend fun readHeaders(): List<String> {
        require(state == State.Header)

        val headers = mutableListOf<String>()

        do {
            val line = bufferedReader.readLineSuspending()
            if (line.isBlank()) {
                state = if (hasBody) State.Body else State.End
            } else {
                headers.add(line)
            }
        } while (state == State.Header)

        return headers
    }

    suspend fun readBody(): String? {
        if (state != State.Body) return null

        // TODO これだと微妙。Content-Length を使うべき
        return bufferedReader.readLineSuspending()
    }

}

private enum class State {
    RequestLine, Header, Body, End
}

