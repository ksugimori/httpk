package httpk.core.message

/**
 * HTTP レスポンス
 *
 * @param version HTTP バージョン
 * @param status HTTP ステータス
 * @param headers HTTP ヘッダー
 * @param body レスポンスボディ
 */
data class HttpResponse(
    val version: HttpVersion,
    val status: HttpStatus,
    val headers: HttpHeaders,
    val body: ByteArray
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

    companion object {
        /**
         * HTTP ステータス 200 のレスポンスを作成する。
         */
        fun ok(headers: HttpHeaders, body: ByteArray): HttpResponse {
            return HttpResponse(
                version = HttpVersion.HTTP_1_1,
                status = HttpStatus.OK,
                headers = headers,
                body = body
            )
        }

        /**
         * HTTP ステータス 404 のレスポンスを作成する。
         */
        fun notFound(headers: HttpHeaders, body: ByteArray): HttpResponse {
            return HttpResponse(
                version = HttpVersion.HTTP_1_1,
                status = HttpStatus.NOT_FOUND,
                headers = headers,
                body = body
            )
        }
    }

}