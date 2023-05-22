package httpk.core.message

class HttpHeaders(
    private val headerItems: MutableMap<String, List<String>> = mutableMapOf()
) {
    val contentLength: Int
        get() = this["Content-Length"]?.firstOrNull()?.toIntOrNull() ?: 0

    operator fun set(key: String, value: Any) {
        add(HttpHeaderItem(key, value.toString()))
    }

    operator fun get(key: String): List<String>? {
        return headerItems[key]
    }

    fun add(item: HttpHeaderItem) {
        this.headerItems[item.key] = item.values
    }

    override fun toString(): String {
        return this.headerItems.toString()
    }

    fun forEach(action: (HttpHeaderItem) -> Unit) {
        this.headerItems.forEach { (key, values) ->
            action(HttpHeaderItem(key, values))
        }
    }
}