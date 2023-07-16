package httpk.http.io

import httpk.exception.InvalidHttpMessageException
import httpk.http.semantics.HttpHeaders
import httpk.http.semantics.HttpMethod
import httpk.http.semantics.HttpRequest
import httpk.http.semantics.HttpVersion
import httpk.util.linesSequence
import httpk.util.readLine
import java.io.InputStream
import java.net.URI

/**
 * HTTP リクエストを読み込む Reader
 *
 * @param inputStream 入力ストリーム
 */
class HttpReader(private val inputStream: InputStream) {
    /**
     * HTTP リクエストを読み込む。
     *
     * @throws InvalidHttpMessageException メッセージが不正なフォーマットである場合
     * @return HTTP リクエスト
     */
    fun readRequest(): HttpRequest {

        val (method, target, version) = parseRequestLine(inputStream.readLine())

        val headers = HttpHeaders()
        inputStream.linesSequence()
            .takeWhile { it.isNotBlank() }
            .map { parseFieldLine(it) }
            .forEach { (name, values) -> headers.addAll(name, values.split(", ?".toRegex())) }

        val body = if (headers.contentLength > 0) {
            inputStream.readNBytes(headers.contentLength)
        } else {
            ByteArray(0)
        }

        return HttpRequest(
            method = HttpMethod.from(method),
            target = URI.create(target),
            version = HttpVersion.from(version),
            headers = headers,
            body = body
        )
    }

    /**
     * Request Line を解析して HTTP メソッド、ターゲット、HTTP バージョンに分解する。
     *
     * @param line request line
     * @return (method, target, version)
     */
    private fun parseRequestLine(line: String): MatchResult.Destructured {
        return "^(\\S+) (\\S+) (\\S+)$".toRegex().matchEntire(line)?.destructured
            ?: throw InvalidHttpMessageException("invalid request line: $line")
    }

    /**
     * Field Line を解析して フィールド名、フィールドの値リスト に分解する。
     *
     * @param line field line
     * @return (fieldName, fieldValueList)
     */
    private fun parseFieldLine(line: String): MatchResult.Destructured {
        return "^(\\S+): (.*)$".toRegex().matchEntire(line)?.destructured
            ?: throw InvalidHttpMessageException("invalid field line: $line")
    }
}