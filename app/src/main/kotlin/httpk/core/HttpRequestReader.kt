package httpk.core

import httpk.util.getGroup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader

class HttpRequestReader(private val bufferedReader: BufferedReader) {
    private var state: State = State.Init

    private enum class State {
        Init, Header, Body, End
    }

    suspend fun readRequestLine(): RequestLine {
        require(state == State.Init)

        val line = readLineSuspend(bufferedReader)
        val result = parseRequestLine(line)

        state = State.Header

        return result
    }

    suspend fun readHeaders(): List<String> {
        require(state == State.Header)

        val resultList = mutableListOf<String>()

        do {
            val line = readLineSuspend(bufferedReader)
            if (line.isBlank()) state = State.End else resultList.add(line)
        } while (state == State.Header)

        return resultList
    }

    suspend fun readBody(): String? {
        if (state != State.Body) return null

        return null
    }
}

data class RequestLine(val method: HttpMethod, val path: String, val version: HttpVersion)

private val REQUEST_LINE_REGEX = """^(?<method>[A-Z]+) (?<path>[A-Z0-9/.~_-]+) (?<version>[A-Z0-9/.]+)$"""
    .toRegex(RegexOption.IGNORE_CASE)

private fun parseRequestLine(requestLine: String): RequestLine {
    return REQUEST_LINE_REGEX.matchEntire(requestLine)?.let {
        RequestLine(
            method = HttpMethod.from(it.getGroup("method")),
            path = it.getGroup("path"),
            version = HttpVersion.from(it.getGroup("version"))
        )
    } ?: throw RuntimeException("Not a HTTP request.") // TODO 例外作成

    // TODO ヘッダー、ボディ
}

private suspend fun readLineSuspend(reader: BufferedReader): String {
    return withContext(Dispatchers.IO) { reader.readLine() }
}