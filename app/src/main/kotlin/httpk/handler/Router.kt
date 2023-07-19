package httpk.handler

import java.net.URI
import java.nio.file.Path

interface Router {
    fun getHandler(uri: URI): HttpHandler
}

class DefaultRouter(private val documentRoot: Path) : Router {
    override fun getHandler(uri: URI): HttpHandler {
        return when {
            uri.path.startsWith("/api") -> ApiHandler()
            else -> StaticResourceHandler(documentRoot)
        }
    }
}