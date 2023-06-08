package httpk.core.message

class HttpHeaders(
    private val headerItems: MutableMap<String, List<String>> = mutableMapOf()
) {
    val contentLength: Int
        get() = this["Content-Length"]?.firstOrNull()?.toIntOrNull() ?: 0

    operator fun set(key: String, value: Any) {
        headerItems[key] = HttpHeaderParser.splitByComma(value.toString())
    }

    operator fun get(key: String): List<String>? {
        return headerItems[key]
    }

    override fun toString(): String {
        return this.headerItems.toString()
    }

    fun forEach(action: (Pair<String, List<String>>) -> Unit) {
        this.headerItems.toList().forEach(action)
    }
}