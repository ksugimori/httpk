package httpk.core.message

class HttpHeaderMap(
    private val map: MutableMap<String, List<String>> = mutableMapOf()
) {
    val contentLength: Int
        get() = this["Content-Length"]?.firstOrNull()?.toIntOrNull() ?: 0

    operator fun set(key: String, value: String) {
        add(HttpHeader(key, value))
    }

    operator fun get(key: String): List<String>? {
        return map[key.lowercase()]
    }

    fun add(item: HttpHeader) {
        this.map[item.key.lowercase()] = item.valueAsList
    }


}