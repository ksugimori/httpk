package httpk.util

fun MatchResult.getGroup(name: String): String {
    return this.groups[name]?.value ?: ""
}
