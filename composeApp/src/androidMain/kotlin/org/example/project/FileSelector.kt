package org.example.project

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream

actual class FileSelector {
    private lateinit var activity: ComponentActivity
    private var onFileSelectedCallback: ((ByteArray, String) -> Unit)? = null
    
    fun init(activity: ComponentActivity) {
        this.activity = activity
    }

    actual fun openFileSelector(onFileSelected: (ByteArray, String) -> Unit) {
        if (!::activity.isInitialized) {
            throw IllegalStateException("FileSelector must be initialized with an activity first")
        }
        onFileSelectedCallback = onFileSelected
        
        // 创建文件选择器意图
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "*/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        
        // 启动文件选择器
        activity.startActivityForResult(
            Intent.createChooser(intent, "Select Logan Log File"),
            REQUEST_CODE_PICK_FILE
        )
    }
    
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CODE_PICK_FILE -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    handleFileSelection(data.data)
                }
            }
            REQUEST_CODE_SAVE_FILE -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    handleFileSave(data.data)
                }
            }
        }
    }
    
    private fun handleFileSelection(uri: Uri?) {
        uri?.let { inputUri ->
            // 创建保存文件的意图
            val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "*/*"
                putExtra(Intent.EXTRA_TITLE, "parsed_log.log")
            }
            
            // 保存选中的输入文件 URI
            selectedInputUri = inputUri
            
            // 启动保存文件选择器
            activity.startActivityForResult(intent, REQUEST_CODE_SAVE_FILE)
        }
    }
    
    private fun handleFileSave(uri: Uri?) {
        uri?.let { outputUri ->
            selectedInputUri?.let { inputUri ->
                processFile(inputUri, outputUri)
            }
        }
    }
    
    private fun processFile(inputUri: Uri, outputUri: Uri) {
        val encryptKey16 = "0123456789012345"
        val encryptIv16 = "0123456789012345"
        
        try {
            activity.contentResolver.openInputStream(inputUri)?.use { inputStream ->
                val uncompressBytesArray = ByteArrayOutputStream()
                val loganParser = LoganParser(
                    encryptKey16.toByteArray(),
                    encryptIv16.toByteArray()
                )
                
                loganParser.parse(inputStream, uncompressBytesArray)
                val resultBytes = uncompressBytesArray.toByteArray()
                
                // 写入输出文件
                activity.contentResolver.openOutputStream(outputUri)?.use { outputStream ->
                    outputStream.write(resultBytes)
                }
                
                // 获取输出文件的路径
                val outputPath = getPathFromUri(outputUri) ?: outputUri.toString()
                onFileSelectedCallback?.invoke(resultBytes, outputPath)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun getPathFromUri(uri: Uri): String? {
        val projection = arrayOf("_data")
        activity.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndexOrThrow("_data")
                return cursor.getString(columnIndex)
            }
        }
        return null
    }
    
    companion object {
        private const val REQUEST_CODE_PICK_FILE = 1001
        private const val REQUEST_CODE_SAVE_FILE = 1002
        private var selectedInputUri: Uri? = null
    }
}
