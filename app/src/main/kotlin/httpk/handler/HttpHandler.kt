package httpk.handler

import httpk.exception.ResourceNotFoundException
import httpk.http.semantics.*

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

