package httpk.handler

import httpk.http.semantics.*
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
class StaticResourceHandler(
    private val documentRoot: Path,
) : HttpHandler() {
    private val errorDirectory: Path = documentRoot.resolve("error")

    override fun doHandle(request: HttpRequest): HttpResponse {
        var path = Path.of(documentRoot.pathString, request.target.path)
        if (path.isDirectory()) {
            path = path.resolve("index.html")
        }

        return if (Files.exists(path)) {
            buildResponse(HttpStatus.OK, path)
        } else {
            buildResponse(HttpStatus.NOT_FOUND, errorDirectory.resolve("404.html"))
        }


    }

    /**
     * HTMLファイルをボディに持つ HTTP レスポンスを組み立てる。
     */
    private fun buildResponse(status: HttpStatus, filePath: Path): HttpResponse {
        val body = Files.readAllBytes(filePath)

        val headers = HttpHeaders()
        headers.contentType = MimeType.fromPath(filePath)
        headers.contentLength = body.size

        return HttpResponse(status = status, headers = headers, body = body)
    }
}