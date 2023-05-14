package httpk.core.message

import httpk.exception.InvalidHttpMessageException
import httpk.util.groupValue

class HttpHeaderItem(
    val key: String,
    val value: String
) {
    val valueAsList: List<String>
        get() = value.split(", *".toRegex())

    companion object {
        private val REGEX = """^(?<key>[a-z-]+): +(?<value>.*)$""".toRegex(RegexOption.IGNORE_CASE)

        fun parse(line: String): HttpHeaderItem {
            return REGEX.matchEntire(line)?.let {
                HttpHeaderItem(
                    key = it.groupValue("key"),
                    value = it.groupValue("value")
                )
            } ?: throw InvalidHttpMessageException("invalid header \"$line\"")
        }
    }

}
