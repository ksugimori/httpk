package httpk.core.message

class HttpHeaders(
    private val headers: MutableMap<String, List<String>> = mutableMapOf()
) : Map<String, List<String>> by headers {
    constructor(builderAction: HttpHeaders.() -> Unit) : this() {
        builderAction()
    }

    val contentLength: Int
        get() = headers["Content-Length"]?.firstOrNull()?.toIntOrNull() ?: 0

    fun add(headerName: String, headerValue: Any) {
        headers[headerName] = HttpHeaderParser.splitByComma(headerValue.toString())
    }

    fun addAll(headerName: String, headerValues: List<String>) {
        headers[headerName] = headerValues
    }

    override fun toString(): String {
        return headers.toString()
    }
}