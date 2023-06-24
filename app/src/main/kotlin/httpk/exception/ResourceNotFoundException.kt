package httpk.exception

class ResourceNotFoundException(
    override val message: String?,
    override val cause: Throwable? = null,
) : HttpkException(message, cause)