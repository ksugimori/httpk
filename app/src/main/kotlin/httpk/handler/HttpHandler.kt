package httpk.handler

import httpk.http.semantics.HttpRequest
import httpk.http.semantics.HttpResponse

/**
 * HTTP ハンドラ。
 */
interface HttpHandler {

    /**
     * HTTP リクエストの処理を行う。
     *
     * @param request HTTP リクエスト
     * @return HTTP レスポンス
     */
    fun handle(request: HttpRequest): HttpResponse
}

