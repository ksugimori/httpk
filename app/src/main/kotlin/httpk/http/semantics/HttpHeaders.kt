package httpk.http.semantics

/**
 * HTTP Headers
 *
 * @param nameToValues 内部的に情報を保持するマップ
 */
class HttpHeaders(
    private val nameToValues: MutableMap<String, MutableList<String>> = mutableMapOf()
) : Map<String, List<String>> by nameToValues {
    constructor(builderAction: HttpHeaders.() -> Unit) : this() {
        builderAction()
    }

    // --------------------------------------------------------------------------
    // computed property
    //   よく使うヘッダーは直接更新しなくても済むようにラップしたプロパティを定義
    // --------------------------------------------------------------------------

    /**
     * Content-Type
     */
    var contentType: String
        get() {
            return nameToValues["Content-Type"]?.first().orEmpty()
        }
        set(value) {
            if (value.isBlank()) return
            add("Content-Type", value)
        }

    /**
     * Content-Length
     */
    var contentLength: Int
        get() {
            return nameToValues["Content-Length"]?.firstOrNull()?.toIntOrNull() ?: 0
        }
        set(value) {
            add("Content-Length", value.toString())
        }

    // --------------------------------------------------------------------------
    // method
    // --------------------------------------------------------------------------

    /**
     * ヘッダーを追加します。
     *
     * すでに存在するヘッダーの場合、リストの最後尾に値が追加されます。
     * @param headerName ヘッダー名
     * @param headerValue ヘッダーの値
     */
    fun add(headerName: String, headerValue: String) {
        val list = nameToValues.getOrPut(headerName) { mutableListOf() }
        list.add(headerValue)
    }

    /**
     * ヘッダーの値をまとめて追加します。
     *
     * すでに存在するヘッダーの場合、リストの最後尾に値がまとめて追加されます。
     * @param headerName ヘッダー名
     * @param headerValues ヘッダーの値
     */
    fun addAll(headerName: String, headerValues: List<String>) {
        val list = nameToValues.getOrPut(headerName) { mutableListOf() }
        list.addAll(headerValues)
    }

    override fun equals(other: Any?): Boolean {
        return if (other is HttpHeaders) this.nameToValues == other.nameToValues else false
    }

    override fun hashCode(): Int {
        return nameToValues.hashCode()
    }

    override fun toString(): String {
        return nameToValues.map { "${it.key}:${it.value}" }.joinToString(separator = ", ", prefix = "{", postfix = "}")
    }
}