package httpk.core.io

import httpk.core.message.*
import httpk.core.regex.groupValue
import httpk.exception.InvalidHttpMessageException
import java.io.InputStream

private val REQUEST_LINE_REGEX =
    """^(?<method>[A-Z]+) (?<path>[A-Z0-9/.~_-]+) (?<version>[A-Z0-9/.]+)$""".toRegex(RegexOption.IGNORE_CASE)
private val HEADER_LINE_REGEX =
    """^(?<key>[A-Z-]+): +(?<value>.*)$""".toRegex(RegexOption.IGNORE_CASE)

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
        return REQUEST_LINE_REGEX.matchEntire(line)?.let {
            val method = HttpMethod.from(it.groupValue("method"))
            val path = it.groupValue("path")
            val version = HttpVersion.from(it.groupValue("version"))
            Triple(method, path, version)
        } ?: throw InvalidHttpMessageException(line)
    }

    private fun parseHeaderLine(line: String): Pair<String, List<String>> {
        return HEADER_LINE_REGEX.matchEntire(line)
            ?.let { Pair(it.groupValue("key"), splitByComma(it.groupValue("value"))) }
            ?: throw InvalidHttpMessageException("invalid header \"$line\"")
    }

    // TODO もしかするとこれ不要？
    private fun splitByComma(value: String): List<String> = value.split(", *".toRegex())

}