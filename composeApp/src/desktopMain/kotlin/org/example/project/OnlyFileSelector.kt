package org.example.project

import java.awt.FileDialog
import java.awt.Frame
import java.io.File

actual class OnlyFileSelector {

    actual fun chooseFilePath(onFinish: (String) -> Unit){
        val fileDialog = FileDialog(Frame()).apply {
            mode = FileDialog.LOAD
            title = "Select Logan Log File"
            isVisible = true
        }

        fileDialog.file?.let { filename ->
            val selectedFile = File(fileDialog.directory, filename)
            onFinish(selectedFile.absolutePath)
        }
    }


}