package httpk.core.io

import httpk.core.message.HttpHeaders
import httpk.core.message.HttpMethod
import httpk.core.message.HttpRequest
import httpk.core.message.HttpVersion
import httpk.core.regex.groupValue
import httpk.exception.InvalidHttpMessageException
import java.io.InputStream

class HttpReader(private val inputStream: InputStream) {
    fun readRequest(): HttpRequest {

        val (method, path, version) = parseRequestLine(inputStream.readLine())

        val headers = HttpHeaders()
        inputStream.linesSequence()
            .takeWhile { it.isNotBlank() }
            .forEach {
                val (name, values) = parseHeaderLine(it)
                headers.addAll(name, values)
            }

        val body = if (headers.contentLength > 0) {
            String(inputStream.readNBytes(headers.contentLength))
        } else {
            null
        }

        return HttpRequest(
            method = method,
            path = path,
            version = version,
            headers = headers,
            body = body
        )
    }

    private fun parseRequestLine(line: String): Triple<HttpMethod, String, HttpVersion> {
        return REQUEST_LINE_REGEX.matchEntire(line)
            ?.let {
                Triple(
                    HttpMethod.from(it.groupValue("method")),
                    it.groupValue("path"),
                    HttpVersion.from(it.groupValue("version"))
                )
            }
            ?: throw InvalidHttpMessageException(line)
    }

    private fun parseHeaderLine(line: String): Pair<String, List<String>> {
        return HEADER_LINE_REGEX.matchEntire(line)
            ?.let {
                Pair(
                    it.groupValue("key"),
                    it.groupValue("value").split(HEADER_VALUE_DELIMITER_REGEX)
                )
            }
            ?: throw InvalidHttpMessageException("invalid header \"$line\"")
    }

    companion object {
        private val REQUEST_LINE_REGEX =
            "^(?<method>[A-Z]+) (?<path>[A-Z0-9/.~_-]+) (?<version>[A-Z0-9/.]+)$".toRegex(RegexOption.IGNORE_CASE)
        private val HEADER_LINE_REGEX = "^(?<key>[A-Z-]+): +(?<value>.*)$".toRegex(RegexOption.IGNORE_CASE)
        private val HEADER_VALUE_DELIMITER_REGEX = ", *".toRegex()
    }
}