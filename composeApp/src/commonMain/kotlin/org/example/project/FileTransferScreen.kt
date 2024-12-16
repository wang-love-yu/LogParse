package org.example.project

import NetworkHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun FileTransferScreen(onBack: () -> Unit) {
    var selectedFilePath by remember { mutableStateOf<String?>(null) }
    val fileSelector = remember { OnlyFileSelector() }
    var ipAddress by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val networkHandler = remember { NetworkHandler() }

    Column(modifier = Modifier.padding(16.dp)) {
        // 返回按钮
        Button(onClick = onBack) {
            Text("返回")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 原有的文件传输功能
        Text("Enter IP Address:")
        TextField(
            value = ipAddress,
            onValueChange = { ipAddress = it },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Selected File: $selectedFilePath")
        Button(onClick = {
            fileSelector.chooseFilePath { outputPath ->
                selectedFilePath = outputPath
            }
        }) {
            Text("Select File")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            coroutineScope.launch {
                if (networkHandler.connect(ipAddress)) {
                    val success = networkHandler.sendFile(selectedFilePath ?: "")
                    if (success) {
                        println("File sent successfully!")
                    } else {
                        println("Failed to send file.")
                    }
                } else {
                    println("Failed to connect to $ipAddress.")
                }
            }
        }) {
            Text("Send File")
        }
    }
}