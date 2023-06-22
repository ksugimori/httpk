package httpk.core.io

import httpk.core.message.HttpResponse
import java.io.OutputStream
import java.io.PrintWriter

private const val CRLF = "\r\n"

class HttpWriter(private val outputStream: OutputStream) {

    fun writeResponse(response: HttpResponse) {
        val writer = PrintWriter(outputStream)

        // status line
        writer.append(statusLine(response)).append(CRLF)

        // headers
        response.headers.forEach { (name, values) ->
            writer.append(fieldLine(name, values)).append(CRLF)
        }

        // ヘッダーとボディの区切りの空行
        writer.print(CRLF)

        writer.flush()

        // body
        outputStream.write(response.body)
    }

    private fun statusLine(response: HttpResponse): String {
        return "${response.version} ${response.status.code} ${response.status.message}"
    }

    private fun fieldLine(name: String, values: List<String>): String {
        return "$name: ${values.joinToString(", ")}"
    }
}