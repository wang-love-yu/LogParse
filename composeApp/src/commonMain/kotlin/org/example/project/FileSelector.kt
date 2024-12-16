package org.example.project

expect class FileSelector() {
    fun openFileSelector(onFileSelected: (ByteArray, String) -> Unit)
}
