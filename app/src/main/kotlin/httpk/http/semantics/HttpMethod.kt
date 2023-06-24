package httpk.http.semantics

import httpk.exception.InvalidHttpMessageException

enum class HttpMethod {
    GET, POST, PUT, DELETE, HEAD, OPTIONS, PATCH;

    companion object {
        fun from(name: String): HttpMethod {
            return values().find { it.name == name }
                ?: throw InvalidHttpMessageException("invalid HTTP method: $name")
        }
    }
}

