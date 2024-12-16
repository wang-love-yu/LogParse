package org.example.project.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.net.URL

actual class NetworkDownloader {
    actual suspend fun downloadFile(
        urlString: String,
        onProgress: (Float) -> Unit,
        onComplete: (ByteArray) -> Unit
    ) = withContext(Dispatchers.IO) {
        try {
            val url = URL(urlString)
            val connection = url.openConnection()
            val fileSize = connection.contentLength.toLong()
            var downloadedSize = 0L
            
            val outputStream = ByteArrayOutputStream()
            
            url.openStream().use { input ->
                val buffer = ByteArray(8192)
                var bytesRead: Int
                
                while (input.read(buffer).also { bytesRead = it } != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                    downloadedSize += bytesRead
                    onProgress(downloadedSize.toFloat() / fileSize)
                }
            }
            
            onComplete(outputStream.toByteArray())
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }
} 