package httpk.core.message

import httpk.exception.InvalidHttpMessageException
import httpk.util.groupValue

private fun String.splitByComma(): List<String> = this.split(", *".toRegex())

data class HttpHeaderItem(
    val key: String,
    val values: List<String>,
) {
    constructor(key: String, value: String) : this(key, value.splitByComma())

    override fun toString(): String {
        return "${key}: ${values.joinToString(", ")}"
    }

    companion object {
        private val REGEX = """^(?<key>[a-z-]+): +(?<value>.*)$""".toRegex(RegexOption.IGNORE_CASE)

        fun parse(line: String): HttpHeaderItem {
            return REGEX.matchEntire(line)?.let {
                HttpHeaderItem(
                    key = it.groupValue("key"),
                    values =  it.groupValue("value").splitByComma()
                )
            } ?: throw InvalidHttpMessageException("invalid header \"$line\"")
        }
    }

}
