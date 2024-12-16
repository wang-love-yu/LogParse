package org.example.project

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import logparse.composeapp.generated.resources.Res
import logparse.composeapp.generated.resources.compose_multiplatform
import org.jetbrains.compose.resources.painterResource
import androidx.compose.material.LinearProgressIndicator
import org.example.project.network.NetworkDownloader

@Composable
fun LogParserScreen(onBack: () -> Unit) {

    var selectedFilePath by remember { mutableStateOf<String?>(null) }
    var isProcessing by remember { mutableStateOf(false) }
    var processComplete by remember { mutableStateOf(false) }
    val fileSelector = remember { FileSelector() }
    val scope = rememberCoroutineScope()
    var netWorkPath by remember { mutableStateOf("") }
    //ase加密Key
    val encryptKey16 = "0123456789012345"
    //aes加密IV
    val encryptIv16 = "0123456789012345"
    var downloadProgress by remember { mutableStateOf(0f) }
    val networkDownloader = remember { NetworkDownloader() }
    val fileSaveSelector = remember { FileSaveSelector() }

        Column(
            Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(onClick = onBack) {
                Text("返回")
            }

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
                Text("选择本地日志文件")
            }
            Row(modifier = Modifier.fillMaxWidth()){
                TextField(
                    value = netWorkPath,
                    onValueChange = { netWorkPath = it },
                    label = { Text("直接输入云端地址") },
                    modifier = Modifier.weight(1f),
                )
                Spacer(modifier = Modifier.padding(start = 10f.dp))
                Button(
                    onClick = {
                        if (netWorkPath.isNotEmpty()) {
                            isProcessing = true
                            processComplete = false
                            downloadProgress = 0f
                            
                            scope.launch {
                                try {
                                    networkDownloader.downloadFile(
                                        netWorkPath,
                                        onProgress = { progress ->
                                            downloadProgress = progress
                                        },
                                        onComplete = { fileBytes ->
                                            // 使用 FileSaveSelector 保存文件

                                            fileSaveSelector.saveFile(
                                                fileBytes,
                                                "downloaded_log.log"
                                            ) { outputPath ->
                                                selectedFilePath = outputPath
                                                isProcessing = false
                                                processComplete = true
                                            }
                                        }
                                    )
                                } catch (e: Exception) {
                                    isProcessing = false
                                    // TODO: 显示错误信息
                                }
                            }
                        }
                    },
                    enabled = !isProcessing && netWorkPath.isNotEmpty()
                ) {
                    Text("下载")
                }
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

            if (isProcessing && downloadProgress > 0) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LinearProgressIndicator(
                        progress = downloadProgress,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text("下载进度: ${(downloadProgress * 100).toInt()}%")
                }
            }
        }
}