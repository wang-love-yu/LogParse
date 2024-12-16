package org.example.project

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class FileSaveSelector() {
     fun saveFile(
        fileBytes: ByteArray,
        defaultFileName: String,
        onComplete: (String) -> Unit
    )
}
