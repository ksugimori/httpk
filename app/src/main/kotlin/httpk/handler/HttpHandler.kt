package httpk.handler

import httpk.core.message.HttpRequest
import httpk.core.message.HttpResponse

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

