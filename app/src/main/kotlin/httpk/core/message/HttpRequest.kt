package httpk.core.message

data class HttpRequest(
    val method: HttpMethod,
    val path: String,
    val version: HttpVersion,
    val headers: HttpHeaders,
    val body: ByteArray
) {
    val bodyAsString: String
        get() = body.decodeToString().orEmpty()

    // ---------------------------------------------------
    // data class で自動的に作られる equals は配列の参照しか比較しない
    // 配列の要素を比較するためにオーバーライド
    // ---------------------------------------------------

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as HttpRequest

        if (method != other.method) return false
        if (path != other.path) return false
        if (version != other.version) return false
        if (headers != other.headers) return false
        return body.contentEquals(other.body)
    }

    override fun hashCode(): Int {
        var result = method.hashCode()
        result = 31 * result + path.hashCode()
        result = 31 * result + version.hashCode()
        result = 31 * result + headers.hashCode()
        result = 31 * result + body.contentHashCode()
        return result
    }
}