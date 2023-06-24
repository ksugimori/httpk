package httpk.handler

import httpk.http.semantics.HttpHeaders
import httpk.http.semantics.HttpRequest
import httpk.http.semantics.HttpResponse
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.isDirectory
import kotlin.io.path.pathString

/**
 * 静的コンテンツにアクセスするための HTTP ハンドラ。
 *
 * @param documentRoot ドキュメントルート
 */
class StaticResourceHandler(private val documentRoot: Path) : HttpHandler {
    override fun handle(request: HttpRequest): HttpResponse {
        var path = Path.of(documentRoot.pathString, request.target.path)
        if (path.isDirectory()) {
            path = path.resolve("index.html")
        }

        val body = try {
            Files.readAllBytes(path)
        } catch (ex: IOException) {
            val body = "<!DOCTYPE html><html><body><h1>NOT FOUND</h1></body></html>".toByteArray()
            val headers = HttpHeaders {
                contentType = "text/html"
                contentLength = body.size
            }
            return HttpResponse.notFound(headers, body)
        }

        // TODO MIME types
        val headers = HttpHeaders()
        headers.contentType = "text/html"
        headers.contentLength = body.size

        return HttpResponse.ok(headers, body)
    }
}