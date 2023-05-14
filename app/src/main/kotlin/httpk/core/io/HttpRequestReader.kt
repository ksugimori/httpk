package httpk.core.io

import httpk.core.message.HttpHeaderItem
import httpk.core.message.HttpHeaders
import httpk.core.message.RequestLine
import httpk.util.readLineSuspending
import java.io.BufferedReader
import java.io.Closeable
import java.io.InputStream
import java.nio.charset.StandardCharsets

class HttpRequestReader(private val bufferedReader: BufferedReader) : Closeable by bufferedReader {
    private var state: State = State.RequestLine
    private var contentLength: Int = 0

    suspend fun readRequestLine(): RequestLine {
        require(state == State.RequestLine)

        val line = bufferedReader.readLineSuspending()
        val requestLine = RequestLine.parse(line)

        state = State.Header

        return requestLine
    }

    suspend fun readHeaders(): HttpHeaders {
        require(state == State.Header)

        val headers = HttpHeaders()

        do {
            val line = bufferedReader.readLineSuspending()
            if (line.isBlank()) {
                state = if (headers.contentLength > 0) State.Body else State.End
            } else {
                headers.add(HttpHeaderItem.parse(line))
            }
        } while (state == State.Header)

        this.contentLength = headers.contentLength

        return headers
    }

    suspend fun readBody(): String? {
        if (state != State.Body) return null

        // TODO contentLength は byte 数なので ascii 以外だとずれる。
        val charArray = CharArray(contentLength)
        bufferedReader.read(charArray)

        return String(charArray)
    }

}

private enum class State {
    RequestLine, Header, Body, End
}

private suspend fun InputStream.readNBytesSuspending(size: Int): ByteArray {
    val inputStream = this
    val byteArray = ByteArray(size)
    inputStream.read(byteArray)
    return byteArray
//    return withContext(Dispatchers.IO) { inputStream.readNBytes(size) }
}