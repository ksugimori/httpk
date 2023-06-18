package httpk.core.message

import httpk.exception.InvalidHttpMessageException
import httpk.core.regex.groupValue

object HttpHeaderParser {
    private val REGEX = """^(?<key>[a-z-]+): +(?<value>.*)$""".toRegex(RegexOption.IGNORE_CASE)

    fun parse(line: String): Pair<String, List<String>> {
        return REGEX.matchEntire(line)
            ?.let { Pair(it.groupValue("key"), splitByComma(it.groupValue("value"))) }
            ?: throw InvalidHttpMessageException("invalid header \"$line\"")
    }

    fun splitByComma(value: String): List<String> = value.split(", *".toRegex())
}

