package httpk.http.semantics

import java.net.URI

/**
 * HTTP リクエスト
 *
 * @param method HTTP メソッド
 * @param target ターゲット
 * @param version HTTP バージョン
 * @param headers HTTP ヘッダー
 * @param body リクエストボディ
 */
data class HttpRequest(
    val method: HttpMethod,
    val target: URI,
    val version: HttpVersion,
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

        other as HttpRequest

        if (method != other.method) return false
        if (target != other.target) return false
        if (version != other.version) return false
        if (headers != other.headers) return false
        return body.contentEquals(other.body)
    }

    override fun hashCode(): Int {
        var result = method.hashCode()
        result = 31 * result + target.hashCode()
        result = 31 * result + version.hashCode()
        result = 31 * result + headers.hashCode()
        result = 31 * result + body.contentHashCode()
        return result
    }
}