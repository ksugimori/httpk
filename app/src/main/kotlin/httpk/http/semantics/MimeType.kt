package httpk.http.semantics

import java.nio.file.Path
import kotlin.io.path.extension

/**
 * 使いそうな MIME Type
 *
 * https://developer.mozilla.org/ja/docs/Web/HTTP/Basics_of_HTTP/MIME_types/Common_types
 */
enum class MimeType(val text: String) {
    CSS("text/css"),
    HTML("text/html"),
    ICON("image/vnd.microsoft.icon"),
    JPEG("image/jpeg"),
    JAVASCRIPT("text/javascript"),
    PNG("image/png"),
    TEXT("text/plain"),
    XML("application/xml"),
    JSON("application/json"),
    OCTET_STREAM("application/octet-stream");

    companion object {
        fun fromText(text: String): MimeType {
            return values().firstOrNull { it.text == text } ?: OCTET_STREAM
        }

        fun fromPath(path: Path): MimeType {
            return when (path.extension.lowercase()) {
                "txt" -> TEXT
                "html", "htm" -> HTML
                "css" -> CSS
                "js" -> JAVASCRIPT
                "ico" -> ICON
                "jpeg", "jpg" -> JPEG
                "png" -> PNG
                "xml" -> XML
                "json" -> JSON
                else -> OCTET_STREAM
            }
        }

    }
}