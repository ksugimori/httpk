package httpk.exception

/**
 * アプリケーション例外
 */
open class HttpkException(
    override val message: String?,
    override val cause: Throwable?
) : RuntimeException(message, cause)