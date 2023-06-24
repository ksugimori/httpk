package httpk.http.semantics

/**
 * HTTP レスポンス
 *
 * @param version HTTP バージョン
 * @param status HTTP ステータス
 * @param headers HTTP ヘッダー
 * @param body レスポンスボディ
 */
data class HttpResponse(
    val version: HttpVersion = HttpVersion.HTTP_1_1,
    val status: HttpStatus = HttpStatus.OK,
    val headers: HttpHeaders = HttpHeaders(),
    val body: ByteArray = ByteArray(0),
) {

    // ---------------------------------------------------
    // data class で自動的に作られる equals は配列の参照しか比較しない
    // 配列の要素を比較するために equals, hashCode をオーバーライド
    // ---------------------------------------------------

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as HttpResponse

        if (version != other.version) return false
        if (status != other.status) return false
        if (headers != other.headers) return false
        return body.contentEquals(other.body)
    }

    override fun hashCode(): Int {
        var result = version.hashCode()
        result = 31 * result + status.hashCode()
        result = 31 * result + headers.hashCode()
        result = 31 * result + body.contentHashCode()
        return result
    }
}