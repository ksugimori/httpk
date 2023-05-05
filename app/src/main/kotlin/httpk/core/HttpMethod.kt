package httpk.core

enum class HttpMethod {
    GET, POST, PUT, DELETE, HEAD, OPTIONS, PATCH;

    companion object {
        fun from(value: String): HttpMethod {
            return values().find { it.name == value }!! // TODO !! なくしたい
        }
    }
}

