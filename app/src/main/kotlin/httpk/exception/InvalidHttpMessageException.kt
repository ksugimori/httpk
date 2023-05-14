package httpk.exception

class InvalidHttpMessageException(value: String) : RuntimeException("invalid HTTP message: $value")