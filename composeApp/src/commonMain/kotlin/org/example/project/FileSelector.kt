package org.example.project

expect class FileSelector() {
    fun openFileSelector(useType:Int,onFileSelected: (ByteArray, String) -> Unit)
}
