package org.example.project

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.awt.FileDialog
import java.awt.Frame
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream

actual class FileSaveSelector {
    actual  fun saveFile(
        fileBytes: ByteArray,
        defaultFileName: String,
        onComplete: (String) -> Unit
    )  {
        val fileDialog = FileDialog(Frame()).apply {
            mode = FileDialog.SAVE
            file = defaultFileName
            title = "保存文件"
            isVisible = true
        }

        fileDialog.file?.let { filename ->
            val outputFile = File(fileDialog.directory, filename)
            outputFile.writeBytes(fileBytes)
            processFile(fileBytes,outputFile){array,path-> onComplete(path) }
            //onComplete(outputFile.absolutePath)
        }
    }
    private fun processFile(inputFileBytes: ByteArray, outputFile: File, onFileSelected: (ByteArray, String) -> Unit) {
        val encryptKey16 = "0123456789012345"
        val encryptIv16 = "0123456789012345"

        try {
            val uncompressBytesArray = ByteArrayOutputStream()
            val loganParser = LoganParser(
                encryptKey16.toByteArray(),
                encryptIv16.toByteArray()
            )

            if (!outputFile.exists()) {
                outputFile.createNewFile()
            }

            val inputFile = ByteArrayInputStream(inputFileBytes)

            loganParser.parse(inputFile, uncompressBytesArray)
            val resultBytes = uncompressBytesArray.toByteArray()
            outputFile.writeBytes(resultBytes)
            onFileSelected(resultBytes, outputFile.absolutePath)
        } catch (e: Exception) {
            println("Error processing file: ${e.message}")
            e.printStackTrace()
        }
    }
} 