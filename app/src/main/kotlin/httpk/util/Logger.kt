package httpk.util

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * ログの時刻フォーマット。Apache 風。
 */
private val dateTimeFormatter = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss.SSS")
    .withZone(ZoneId.systemDefault())

/**
 * コンソールにログを出力する。
 *
 * @param message ログメッセージ
 */
fun consoleLog(message: String) {
    val timestamp = dateTimeFormatter.format(Instant.now())
    val threadName = Thread.currentThread().name
    println("[$timestamp] [$threadName] $message")
}