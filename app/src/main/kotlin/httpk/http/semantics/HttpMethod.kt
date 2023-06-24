package httpk.http.semantics

enum class HttpMethod {
    GET, POST, PUT, DELETE, HEAD, OPTIONS, PATCH;

    companion object {
        fun from(name: String): HttpMethod {
            return values().find { it.name == name }!! // TODO !! なくしたい
        }
    }
}

