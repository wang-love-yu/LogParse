package org.example.project.network

expect class NetworkDownloader() {
    suspend fun downloadFile(
        urlString: String,
        onProgress: (Float) -> Unit,
        onComplete: (ByteArray) -> Unit
    )
} 