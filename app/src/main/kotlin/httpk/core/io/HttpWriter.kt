package httpk.core.io

import httpk.core.message.HttpResponse
import java.io.OutputStream
import java.io.PrintWriter

private const val CRLF = "\r\n"

class HttpWriter(outputStream: OutputStream) {
    private val writer: PrintWriter

    init {
        writer = PrintWriter(outputStream.bufferedWriter())
    }

    fun writeResponse(response: HttpResponse) {
        // status line
        writer.append(statusLine(response)).append(CRLF)

        // headers
        response.headers.forEach { (headerName, headerValues) ->
          writer.append(headerLine(headerName, headerValues)).append(CRLF)
        }

        // ヘッダーとボディの区切りの空行
        writer.print(CRLF)

        // body
        writer.print(response.body)

        writer.flush()
    }

    private fun statusLine(response: HttpResponse): String {
        return "${response.version} ${response.status.code} ${response.status.message}"
    }

    private fun headerLine(headerName: String, headerValues: List<String>): String {
        val joinedValue = headerValues.joinToString(", ")
        return "$headerName: $joinedValue"
    }
}