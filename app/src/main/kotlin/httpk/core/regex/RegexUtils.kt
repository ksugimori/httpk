package httpk.core.regex

fun MatchResult.groupValue(name: String): String {
    return this.groups[name]?.value ?: ""
}
