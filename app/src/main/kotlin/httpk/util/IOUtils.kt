package httpk.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStream
import java.io.OutputStream
import java.net.ServerSocket
import java.net.Socket

suspend fun BufferedReader.readLineSuspending(): String {
    val reader = this
    return withContext(Dispatchers.IO) { reader.readLine() }
}

suspend fun Socket.getInputStreamSuspending(): InputStream {
    val socket = this
    return withContext(Dispatchers.IO) { socket.getInputStream() }
}

suspend fun Socket.getOutputStreamSuspending(): OutputStream {
    val socket = this
    return withContext(Dispatchers.IO) { socket.getOutputStream() }
}

suspend fun ServerSocket.acceptSuspending(): Socket {
    val serverSocket = this
    return withContext(Dispatchers.IO) { serverSocket.accept() }
}