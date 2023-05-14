package httpk.core.message

enum class HttpVersion(private val value: String) {
    HTTP_0_9("HTTP/0.9"),
    HTTP_1_1("HTTP/1.1"),
    HTTP_2("HTTP/2"),
    HTTP_3("HTTP/3");

    override fun toString(): String = value

    companion object {
        fun from(value: String): HttpVersion {
            return values().find { it.value == value }!!
        }
    }
}