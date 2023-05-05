package httpk.core

import java.lang.RuntimeException

data class RequestLine(val method: HttpMethod, val path: String, val version: HttpVersion)

private val REQUEST_LINE_REGEX = """^(?<method>[A-Z]+) (?<path>[A-Z0-9/.~_-]+) (?<version>[A-Z0-9/.]+)$"""
    .toRegex(RegexOption.IGNORE_CASE)

fun MatchResult.getGroup(name: String): String {
    return this.groups[name]?.value ?: ""
}

fun parseRequestLine(requestLine: String): RequestLine {
    return REQUEST_LINE_REGEX.matchEntire(requestLine)?.let {
        RequestLine(
            method = HttpMethod.from(it.getGroup("method")),
            path = it.getGroup("path"),
            version = HttpVersion.from(it.getGroup("version"))
        )
    } ?: throw RuntimeException("Not a HTTP request.") // TODO 例外作成

    // TODO ヘッダー、ボディ
}