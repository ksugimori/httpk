package httpk.core

import httpk.util.groupValue

data class RequestLine(
    val method: HttpMethod,
    val path: String,
    val version: HttpVersion
) {
    companion object {

        private val REGEX =
            """^(?<method>[A-Z]+) (?<path>[A-Z0-9/.~_-]+) (?<version>[A-Z0-9/.]+)$""".toRegex(RegexOption.IGNORE_CASE)

        fun parse(line: String): RequestLine {
            return REGEX.matchEntire(line)?.let {
                RequestLine(
                    method = HttpMethod.from(it.groupValue("method")),
                    path = it.groupValue("path"),
                    version = HttpVersion.from(it.groupValue("version"))
                )
            } ?: throw RuntimeException("Not a HTTP request.") // TODO 例外作成
        }

    }
}