package httpk.exception

class InvalidHttpMessageException(
    override val message: String?,
    override val cause: Throwable? = null,
) : HttpkException(message, cause)