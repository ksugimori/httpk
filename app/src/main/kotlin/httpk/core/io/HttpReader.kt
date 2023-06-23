package httpk.core.io

import httpk.core.message.HttpHeaders
import httpk.core.message.HttpMethod
import httpk.core.message.HttpRequest
import httpk.core.message.HttpVersion
import httpk.core.regex.groupValue
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
            ?.let {
                Triple(
                    HttpMethod.from(it.groupValue("method")),
                    URI.create(it.groupValue("target")),
                    HttpVersion.from(it.groupValue("version"))
                )
            }
            ?: throw InvalidHttpMessageException(line)
    }

    private fun parseFieldLine(line: String): Pair<String, List<String>> {
        return HEADER_LINE_REGEX.matchEntire(line)
            ?.let {
                Pair(
                    it.groupValue("name"),
                    it.groupValue("value").split(HEADER_VALUE_DELIMITER_REGEX)
                )
            }
            ?: throw InvalidHttpMessageException("invalid header \"$line\"")
    }

    companion object {
        private val REQUEST_LINE_REGEX = """^(?<method>[A-Z]+) (?<target>\S+) (?<version>[A-Z0-9/.]+)$""".toRegex()
        private val HEADER_LINE_REGEX = """^(?<name>[A-Za-z-]+):\s?(?<value>.*)\s?$""".toRegex()
        private val HEADER_VALUE_DELIMITER_REGEX = """,\s*""".toRegex()
    }
}