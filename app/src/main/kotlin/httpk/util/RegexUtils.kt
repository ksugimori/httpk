package httpk.util

fun MatchResult.groupValue(name: String): String {
    return this.groups[name]?.value ?: ""
}
