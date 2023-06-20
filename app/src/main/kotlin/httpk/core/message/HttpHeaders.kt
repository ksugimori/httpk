package httpk.core.message

class HttpHeaders(
    private val headers: MutableMap<String, MutableList<String>> = mutableMapOf()
) : Map<String, List<String>> by headers {
    constructor(builderAction: HttpHeaders.() -> Unit) : this() {
        builderAction()
    }

    override fun equals(other: Any?): Boolean {
        return when (other) {
            is HttpHeaders -> this.headers == other.headers
            else -> false
        }
    }

    override fun toString(): String {
        return headers.map { "${it.key}:${it.value}" }.joinToString(separator = ", ", prefix = "{", postfix = "}")
    }

    fun add(headerName: String, headerValue: String) {
        val list = headers.getOrPut(headerName) { mutableListOf() }
        list.add(headerValue)
    }

    fun addAll(headerName: String, headerValues: List<String>) {
        val list = headers.getOrPut(headerName) { mutableListOf() }
        list.addAll(headerValues)
    }

    var contentType: String
        get() {
            return headers["Content-Type"]?.first().orEmpty()
        }
        set(value) {
            if (value.isNullOrBlank()) return
            add("Content-Type", value)
        }

    var contentLength: Int
        get() {
            return headers["Content-Length"]?.firstOrNull()?.toIntOrNull() ?: 0
        }
        set(value) {
            add("Content-Length", value.toString())
        }
}