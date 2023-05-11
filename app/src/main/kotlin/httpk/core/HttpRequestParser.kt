package httpk.core

import httpk.util.getGroup

class HttpRequestParser() {
    private var state: State = State.Init

    private var requestLine: RequestLine? = null
    private var headers: MutableList<String> = mutableListOf()

    fun read(line: String) {
        state.process(this, line)
    }

    fun isNotCompleted(): Boolean {
        return state != State.End
    }

    fun build(): HttpRequest {
        return requestLine?.let {
            HttpRequest(
                method = it.method,
                path = it.path,
                version = it.version,
                headers = headers,
                body = null
            )
        } ?: throw IllegalStateException("requestLine not initialized")
    }

    private sealed interface State {
        fun process(parser: HttpRequestParser, line: String)

        object Init : State {
            override fun process(parser: HttpRequestParser, line: String) {
                parser.requestLine = parseRequestLine(line)
                parser.state = Header
            }
        }

        object Header : State {
            override fun process(parser: HttpRequestParser, line: String) {
                if (line.isBlank()) {
                    parser.state = End
                } else {
                    parser.headers.add(line)
                    parser.state = Header
                }
            }
        }

        object End : State {
            override fun process(parser: HttpRequestParser, line: String) {}
        }
    }
}

private data class RequestLine(val method: HttpMethod, val path: String, val version: HttpVersion)

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