package httpk.handler

import java.net.URI
import java.nio.file.Path

class Router(private val documentRoot: Path) {
    fun getHandler(uri: URI): HttpHandler {
        return when {
            uri.path.startsWith("/api") -> ApiHandler()
            else -> StaticResourceHandler(documentRoot)
        }
    }
}