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
            .forEach { (name, values) -> headers.addAll(name, values) }

        val body = if (headers.contentLength > 0) {
            inputStream.readNBytes(headers.contentLength)
        } else {
            ByteArray(0)
        }

        return HttpRequest(
            method = method,
            target = target,
            version = version,
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
    private fun parseRequestLine(line: String): Triple<HttpMethod, URI, HttpVersion> {
        return REQUEST_LINE_REGEX.matchEntire(line)
            ?.destructured
            ?.let { (method, target, version) ->
                Triple(
                    HttpMethod.from(method),
                    URI.create(target),
                    HttpVersion.from(version)
                )
            }
            ?: throw InvalidHttpMessageException("invalid request line: $line")
    }

    /**
     * Field Line を解析して フィールド名、フィールドの値リスト に分解する。
     *
     * @param line field line
     * @return (fieldName, fieldValueList)
     */
    private fun parseFieldLine(line: String): Pair<String, List<String>> {
        return FIELD_LINE_REGEX.matchEntire(line)
            ?.destructured
            ?.let { (name, value) ->
                Pair(
                    name,
                    value.split(COMMA_AND_OPTIONAL_SPACE_REGEX)
                )
            }
            ?: throw InvalidHttpMessageException("invalid field line: $line")
    }

    companion object {
        private val REQUEST_LINE_REGEX = """^([A-Z]+) (\S+) ([A-Z0-9/.]+)$""".toRegex()
        private val FIELD_LINE_REGEX = """^([A-Za-z-]+):\s?(.*)\s?$""".toRegex()
        private val COMMA_AND_OPTIONAL_SPACE_REGEX = """,\s*""".toRegex()
    }
}