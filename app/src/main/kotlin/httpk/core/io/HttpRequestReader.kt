package httpk.core.io

import httpk.core.message.HttpHeaderItem
import httpk.core.message.HttpHeaders
import httpk.core.message.RequestLine
import httpk.util.readNBytesSuspending
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.Closeable
import java.io.InputStream

class HttpRequestReader(private val inputStream: InputStream) : Closeable by inputStream {
    private var state: State = State.RequestLine
    private var contentLength: Int = 0

    suspend fun readRequestLine(): RequestLine {
        require(state == State.RequestLine)

        val line = inputStream.readUntilCRLF()
        val requestLine = RequestLine.parse(line)

        state = State.Header

        return requestLine
    }

    suspend fun readHeaders(): HttpHeaders {
        require(state == State.Header)

        val headers = HttpHeaders()

        do {
            val line = inputStream.readUntilCRLF()
            if (line.isBlank()) {
                contentLength = headers.contentLength
                state = if (contentLength > 0) State.Body else State.End
            } else {
                headers.add(HttpHeaderItem.parse(line))
            }
        } while (state == State.Header)

        return headers
    }

    suspend fun readBody(): String? {
        if (state != State.Body) return null

        val bytes = inputStream.readNBytesSuspending(contentLength)
        return String(bytes)
    }

}

private enum class State {
    RequestLine, Header, Body, End
}


private suspend fun InputStream.readUntilCRLF(): String {
    val bytes = ByteArray(1_000)
    var index = 0
    var charCode: Int

    val inputStream = this
    withContext(Dispatchers.IO) {
        while (inputStream.read().also { charCode = it } != -1) {
            if (charCode == '\r'.code) {
                inputStream.skip(1) // ignore LF
                break
            }

            bytes[index++] = charCode.toByte()
        }
    }

    return String(bytes.copyOf(index))
}
