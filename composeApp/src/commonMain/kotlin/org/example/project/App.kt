package org.example.project

import NetworkHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    var selectedFilePath by remember { mutableStateOf<String?>(null) }
    val fileSelector = remember { OnlyFileSelector() }
    var ipAddress by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val networkHandler = remember {  NetworkHandler() } // 假设你已经实现了这个类

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Enter IP Address:")
        TextField(
            value = ipAddress,
            onValueChange = { ipAddress = it },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Selected File: $selectedFilePath")
        Button(onClick = {
            // 调用文件选择器
            fileSelector.chooseFilePath {outputPath ->
                selectedFilePath = outputPath
            }
        }) {
            Text("Select File")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            // 点击发送文件按钮时，调用 NetworkHandler 发送文件
            coroutineScope.launch {
                if (networkHandler.connect(ipAddress)) {
                    val success = networkHandler.sendFile(selectedFilePath ?: "")
                    if (success) {
                        // 发送成功的反馈
                        println("File sent successfully!")
                    } else {
                        // 发送失败的反馈
                        println("Failed to send file.")
                    }
                } else {
                    // 连接失败的反馈
                    println("Failed to connect to $ipAddress.")
                }
            }
        }) {
            Text("Send File")
        }
    }



    //日志解析

/*    var selectedFilePath by remember { mutableStateOf<String?>(null) }
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
    }*/
}