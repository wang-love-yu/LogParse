package org.example.project

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch

import logparse.composeapp.generated.resources.Res
import logparse.composeapp.generated.resources.compose_multiplatform

@Composable
@Preview
fun App() {
    var selectedFilePath by remember { mutableStateOf<String?>(null) }
    var isProcessing by remember { mutableStateOf(false) }
    var processComplete by remember { mutableStateOf(false) }
    val fileSelector = remember { FileSelector() }
    val scope = rememberCoroutineScope()
    
    //ase加密Key
    val encryptKey16 = "0123456789012345"
    //aes加密IV
    val encryptIv16 = "0123456789012345"

    MaterialTheme {
        Column(
            Modifier.fillMaxWidth().padding(16.dp), 
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    isProcessing = true
                    processComplete = false
                    scope.launch(Dispatchers.IO) {
                        fileSelector.openFileSelector { _, outputPath ->
                            selectedFilePath = outputPath
                            isProcessing = false
                            processComplete = true
                        }
                    }
                },
                enabled = !isProcessing
            ) {
                Text("选择日志文件")
            }

            if (isProcessing) {
                Text("Processing...", style = MaterialTheme.typography.body1)
            } else if (processComplete) {
                Text(
                    "处理完成!\n保存到: $selectedFilePath",
                    style = MaterialTheme.typography.body1
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            AnimatedVisibility(selectedFilePath != null) {
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(painterResource(Res.drawable.compose_multiplatform), null)
                    Text("File saved to: $selectedFilePath")
                }
            }
        }
    }
}