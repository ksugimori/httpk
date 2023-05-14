package httpk.core.message

class HttpHeaders(
    private val map: MutableMap<String, List<String>> = mutableMapOf()
) {
    val contentLength: Int
        get() = this["Content-Length"]?.firstOrNull()?.toIntOrNull() ?: 0

    operator fun set(key: String, value: String) {
        add(HttpHeaderItem(key, value))
    }

    operator fun get(key: String): List<String>? {
        return map[key.lowercase()]
    }

    fun add(item: HttpHeaderItem) {
        this.map[item.key.lowercase()] = item.valueAsList
    }


}