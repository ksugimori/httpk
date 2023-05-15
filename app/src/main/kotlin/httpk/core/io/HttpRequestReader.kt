package httpk.core.io

import httpk.core.message.HttpHeaderItem
import httpk.core.message.HttpHeaders
import httpk.core.message.HttpRequest
import httpk.core.message.RequestLine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.Closeable
import java.io.InputStream

class HttpRequestReader(private val inputStream: InputStream) : Closeable by inputStream {
    suspend fun readRequest(): HttpRequest {
        val requestLine = inputStream.readUntilCRLF().let(RequestLine::parse)

        val headers = HttpHeaders()
        while (true) { // TODO これも微妙
            val line = inputStream.readUntilCRLF()
            if (line.isBlank()) break
            HttpHeaderItem.parse(line).let(headers::add)
        }

        val body = if (headers.contentLength > 0) {
            inputStream.readNBytesSuspending(headers.contentLength).let(::String)
        } else {
            null
        }

        return HttpRequest(
            method = requestLine.method,
            path = requestLine.path,
            version = requestLine.version,
            headers = headers,
            body = body
        )
    }

}

private suspend fun InputStream.readNBytesSuspending(size: Int): ByteArray {
    val inputStream = this
    return withContext(Dispatchers.IO) { inputStream.readNBytes(size) }
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
