package httpk.core.io

import httpk.core.message.HttpHeaderParser
import httpk.core.message.HttpHeaders
import httpk.core.message.HttpRequest
import httpk.core.message.RequestLine
import java.io.InputStream

// TODO テスト作成

class HttpReader(private val inputStream: InputStream) {
    fun readRequest(): HttpRequest {

        val requestLine = inputStream.readLine().let { RequestLine.parse(it) }

        val headers = HttpHeaders()
        inputStream.linesSequence()
            .takeWhile { it.isNotBlank() }
            .forEach {
                val (name, values) = HttpHeaderParser.parse(it)
                headers.addAll(name, values)
            }

        val body = if (headers.contentLength > 0) {
            String(inputStream.readNBytes(headers.contentLength))
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