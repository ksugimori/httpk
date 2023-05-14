package httpk.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStream
import java.net.ServerSocket
import java.net.Socket

suspend fun BufferedReader.readLineSuspending(): String {
    val reader = this
    return withContext(Dispatchers.IO) { reader.readLine() }
}

suspend fun InputStream.readNBytesSuspending(size: Int): ByteArray {
    val inputStream = this
    return withContext(Dispatchers.IO) { inputStream.readNBytes(size) }
}

suspend fun InputStream.readLineSuspending(): String {
    val bytes = ByteArray(1_000)
    var index = 0
    var charCode: Int

    val inputStream = this
    withContext(Dispatchers.IO) {
        while (inputStream.read().also { charCode = it } != -1) {
            if (charCode == '\r'.code) {
                inputStream.skip(1) // ignore LF
                break
            }

            bytes[index++] = charCode.toByte()
        }
    }

    return String(bytes.copyOf(index))
}

suspend fun Socket.getInputStreamSuspending(): InputStream {
    val socket = this
    return withContext(Dispatchers.IO) { socket.getInputStream() }
}

suspend fun Socket.getBufferedWriterSuspending(): BufferedWriter {
    val socket = this
    return withContext(Dispatchers.IO) { socket.getOutputStream() }.bufferedWriter()
}

suspend fun ServerSocket.acceptSuspending(): Socket {
    val serverSocket = this
    return withContext(Dispatchers.IO) { serverSocket.accept() }
}