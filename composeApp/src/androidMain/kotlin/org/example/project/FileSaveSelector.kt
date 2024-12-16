package org.example.project



actual class FileSaveSelector {
    actual  fun saveFile(
        fileBytes: ByteArray,
        defaultFileName: String,
        onComplete: (String) -> Unit
    ){
}
}