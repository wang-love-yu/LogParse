package org.example.project

import android.os.Bundle
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
    private val fileSelector = FileSelector()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 初始化 FileSelector
        fileSelector.init(this)
        
        setContent {
            App()
        }
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        fileSelector.onActivityResult(requestCode, resultCode, data)
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}