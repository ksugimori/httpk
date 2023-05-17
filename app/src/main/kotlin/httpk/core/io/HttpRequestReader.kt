package httpk.core.io

import httpk.core.message.HttpHeaderItem
import httpk.core.message.HttpHeaders
import httpk.core.message.HttpRequest
import httpk.core.message.RequestLine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.Closeable
import java.io.InputStream

class HttpRequestReader(private val inputStream: InputStream) : Closeable by inputStream {
    suspend fun readRequest(): HttpRequest = withContext(Dispatchers.IO) {
        val requestLine = inputStream.readUntilCRLF().let(RequestLine::parse)

        val headers = HttpHeaders()
        inputStream.linesSequence()
            .takeWhile(String::isNotBlank)
            .map(HttpHeaderItem::parse)
            .forEach(headers::add)

        val body = if (headers.contentLength > 0) {
            inputStream.readNBytes(headers.contentLength).let(::String)
        } else {
            null
        }

        HttpRequest(
            method = requestLine.method,
            path = requestLine.path,
            version = requestLine.version,
            headers = headers,
            body = body
        )
    }

}

private fun InputStream.readUntilCRLF(): String {
    val out = ByteArrayOutputStream()

    var previous: Int = -1
    var current: Int = -1
    var isCRLF = false
    while (true) {
        current = this.read()
        isCRLF = (previous == '\r'.code && current == '\n'.code)

        if (current == -1 || isCRLF) break

        out.write(current)
        previous = current
    }

    val bytes = out.toByteArray()
    return String(if (isCRLF) bytes.copyOf(bytes.size - 1) else bytes)
}

private fun InputStream.linesSequence() = sequence<String> {
    while (true) { // TODO inputStream の終了を検知したい
        yield(this@linesSequence.readUntilCRLF())
    }
}