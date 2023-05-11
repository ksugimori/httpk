package httpk.core

class HttpRequestParser() {
    private var state: State = State.Init

    var requestLine: RequestLine? = null
    var headers: MutableList<String> = mutableListOf()

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

