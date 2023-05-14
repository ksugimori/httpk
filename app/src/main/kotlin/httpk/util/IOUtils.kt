package httpk.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.BufferedWriter
import java.net.ServerSocket
import java.net.Socket

suspend fun BufferedReader.readLineSuspending(): String {
    val reader = this
    return withContext(Dispatchers.IO) { reader.readLine() }
}

suspend fun Socket.getBufferedReaderSuspending(): BufferedReader {
    val socket = this
    return withContext(Dispatchers.IO) { socket.getInputStream() }.bufferedReader()
}

suspend fun Socket.getBufferedWriterSuspending(): BufferedWriter {
    val socket = this
    return withContext(Dispatchers.IO) { socket.getOutputStream() }.bufferedWriter()
}

suspend fun ServerSocket.acceptSuspending(): Socket {
    val serverSocket = this
    return withContext(Dispatchers.IO) { serverSocket.accept() }
}