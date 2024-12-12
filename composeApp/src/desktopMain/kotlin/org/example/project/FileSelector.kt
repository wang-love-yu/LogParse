package org.example.project

import java.awt.FileDialog
import java.awt.Frame
import java.io.File
import java.io.FileInputStream
import java.io.ByteArrayOutputStream

actual class FileSelector {
    actual fun openFileSelector(onFileSelected: (ByteArray, String) -> Unit) {
        val fileDialog = FileDialog(Frame()).apply {
            mode = FileDialog.LOAD
            title = "Select Logan Log File"
            isVisible = true
        }
        
        fileDialog.file?.let { filename ->
            val selectedFile = File(fileDialog.directory, filename)
            val saveDialog = FileDialog(Frame()).apply {
                mode = FileDialog.SAVE
                file = "parsed_log.log"
                title = "Save Parsed Log File"
                isVisible = true
            }
            
            saveDialog.file?.let { saveFilename ->
                val outputFile = File(saveDialog.directory, saveFilename)
                processFile(selectedFile, outputFile, onFileSelected)
            }
        }
    }
    
    private fun processFile(inputFile: File, outputFile: File, onFileSelected: (ByteArray, String) -> Unit) {
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
            
            loganParser.parse(FileInputStream(inputFile), uncompressBytesArray)
            val resultBytes = uncompressBytesArray.toByteArray()
            outputFile.writeBytes(resultBytes)
            onFileSelected(resultBytes, outputFile.absolutePath)
        } catch (e: Exception) {
            println("Error processing file: ${e.message}")
            e.printStackTrace()
        }
    }
}
