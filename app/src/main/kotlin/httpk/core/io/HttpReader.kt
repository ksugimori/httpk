package httpk.core.io

import httpk.core.message.HttpHeaders
import httpk.core.message.HttpMethod
import httpk.core.message.HttpRequest
import httpk.core.message.HttpVersion
import httpk.exception.InvalidHttpMessageException
import java.io.InputStream
import java.net.URI

class HttpReader(private val inputStream: InputStream) {
    fun readRequest(): HttpRequest {

        val (method, target, version) = parseRequestLine(inputStream.readLine())

        val headers = HttpHeaders()
        inputStream.linesSequence()
            .takeWhile { it.isNotBlank() }
            .map { parseFieldLine(it) }
            .forEach { (name, values) -> headers.addAll(name, values) }

        val body = if (headers.contentLength > 0) {
            inputStream.readNBytes(headers.contentLength)
        } else {
            ByteArray(0)
        }

        return HttpRequest(
            method = method,
            target = target,
            version = version,
            headers = headers,
            body = body
        )
    }

    private fun parseRequestLine(line: String): Triple<HttpMethod, URI, HttpVersion> {
        return REQUEST_LINE_REGEX.matchEntire(line)
            ?.destructured
            ?.let { (method, target, version) ->
                Triple(
                    HttpMethod.from(method),
                    URI.create(target),
                    HttpVersion.from(version)
                )
            }
            ?: throw InvalidHttpMessageException("invalid request line: $line")
    }

    private fun parseFieldLine(line: String): Pair<String, List<String>> {
        return HEADER_LINE_REGEX.matchEntire(line)
            ?.destructured
            ?.let { (name, value) ->
                Pair(
                    name,
                    value.split(HEADER_VALUE_DELIMITER_REGEX)
                )
            }
            ?: throw InvalidHttpMessageException("invalid field line: $line")
    }

    companion object {
        private val REQUEST_LINE_REGEX = """^([A-Z]+) (\S+) ([A-Z0-9/.]+)$""".toRegex()
        private val HEADER_LINE_REGEX = """^([A-Za-z-]+):\s?(.*)\s?$""".toRegex()
        private val HEADER_VALUE_DELIMITER_REGEX = """,\s*""".toRegex()
    }
}