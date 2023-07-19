package httpk.http.semantics

class FormBody private constructor(
    private val internalMap: MutableMap<String, String> = mutableMapOf()
) : Map<String, String> by internalMap {
    constructor(body: ByteArray) : this() {
        val keyValuePairs = body.decodeToString().split("&").forEach {
            val (key, value) = it.split("=")
            internalMap[key] = value
        }
    }
}