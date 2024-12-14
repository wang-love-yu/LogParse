// jvmMain
import java.io.File
import java.io.FileInputStream
import java.net.Socket

actual class NetworkHandler {
    private var socket: Socket? = null

    actual fun connect(ip: String): Boolean {
        return try {
            socket = Socket(ip, 9100) // 端口号可以根据需要调整
            true
        } catch (e: Exception) {
            false
        }
    }

    actual fun sendFile(filePath: String): Boolean {
        return try {
            socket?.getOutputStream()?.use { outputStream ->
                val file = File(filePath)
                val inputStream = FileInputStream(file)
                inputStream.copyTo(outputStream)
                inputStream.close()
            }
            true
        } catch (e: Exception) {
            false
        }
    }
}