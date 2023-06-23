package httpk.core.io

import httpk.core.message.HttpResponse
import java.io.OutputStream
import java.io.PrintWriter

private const val SP = " "
private const val CRLF = "\r\n"

/**
 * HTTP レスポンスを書き出す Writer
 *
 * @param outputStream 出力先ストリーム
 */
class HttpWriter(private val outputStream: OutputStream) {

    /**
     * HTTP レスポンスを書き出す。
     *
     * @param response HTTP レスポンス
     */
    fun writeResponse(response: HttpResponse) {
        val writer = PrintWriter(outputStream)

        // status line
        writer.printAll(response.version, SP, response.status.code, SP, response.status.message, CRLF)

        // headers
        for ((fieldName, valueList) in response.headers) {
            val fieldValue = valueList.joinToString(", ")
            writer.printAll(fieldName, ":", SP, fieldValue, CRLF)
        }

        // ヘッダーとボディの区切りの空行
        writer.print(CRLF)

        writer.flush()

        // body
        outputStream.write(response.body)
    }

    /**
     * 可変長引数で受け取ったオブジェクトを全て書き出す。
     *
     * @param objects 出力するオブジェクト
     */
    private fun PrintWriter.printAll(vararg objects: Any) = objects.forEach(this::print)
}