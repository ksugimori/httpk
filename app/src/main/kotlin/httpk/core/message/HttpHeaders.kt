package httpk.core.message

class HttpHeaders(
    private val headers: MutableMap<String, List<String>> = mutableMapOf()
) : Map<String, List<String>> by headers {
    val contentLength: Int
        get() = headers["Content-Length"]?.firstOrNull()?.toIntOrNull() ?: 0

    operator fun set(key: String, value: Any) {
        headers[key] = if (value is String) HttpHeaderParser.splitByComma(value) else listOf(value.toString())
    }

    override fun toString(): String {
        return headers.toString()
    }
}