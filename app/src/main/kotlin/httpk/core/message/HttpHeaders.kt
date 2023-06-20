package httpk.core.message

class HttpHeaders(
    private val headers: MutableMap<String, MutableList<String>> = mutableMapOf()
) : Map<String, List<String>> by headers {
    constructor(builderAction: HttpHeaders.() -> Unit) : this() {
        builderAction()
    }

    fun add(headerName: String, headerValue: String) {
        val list = headers.getOrPut(headerName) { mutableListOf() }
        list.add(headerValue)
    }

    fun addAll(headerName: String, headerValues: List<String>) {
        val list = headers.getOrPut(headerName) { mutableListOf() }
        list.addAll(headerValues)
    }

    var contentType: String?
        get() {
            return headers["Content-Type"]?.firstOrNull()
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