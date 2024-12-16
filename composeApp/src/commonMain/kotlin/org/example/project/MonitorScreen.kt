package org.example.project

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.*
import org.example.project.server.Server

@Composable
fun MonitorScreen(onBack: () -> Unit) {
    var port by remember { mutableStateOf("12345") }
    var ip by remember { mutableStateOf("192.168.31.151") }

    var isServerRunning by remember { mutableStateOf(false) }
    var messages by remember { mutableStateOf(listOf<String>()) }
    val scope = rememberCoroutineScope()
    var serverJob by remember { mutableStateOf<Job?>(null) }

    Column(modifier = Modifier.padding(16.dp)) {
        Button(onClick = onBack) {
            Text("返回")
        }
        TextField(
            value = ip,
            onValueChange = { ip = it },
            label = { Text("ip地址") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // 端口输入
        TextField(
            value = port,
            onValueChange = { port = it },
            label = { Text("端口号") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 启动/停止服务器按钮
        Button(
            onClick = {
                    serverJob = scope.launch {
                        startServer(
                            ip = ip,
                            port = port.toIntOrNull() ?: 8080,
                            onMessageReceived = { message ->
                                print(message)
                                messages = messages + message

                            }
                        )
                    }
                    isServerRunning = true
                 /*else {
                    serverJob?.cancel()
                    isServerRunning = false
                }*/
            }
        ) {
            Text(if (isServerRunning) "停止服务器" else "启动服务器")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 显示服务器状态
        Text("服务器状态: ${if (isServerRunning) "运行中" else "已停止"}")

        Spacer(modifier = Modifier.height(16.dp))

        // 显示接收到的消息
        LazyColumn {
            items(messages) { message ->
                Text(
                    text = message,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}

private suspend fun startServer(
    ip:String,
    port: Int,
    onMessageReceived: (String) -> Unit
) = withContext(Dispatchers.IO) {
    try {
        val server = Server(ip,port)
        println("服务器启动在端口: $port")
        onMessageReceived("服务器启动在端口: $port")


            try {
                val client = server.startServer{ status,message ->
                    if (status){

                        server.receiveMessage{ message ->
                            onMessageReceived(message)
                        }
                    }else{

                    }
                }

            } catch (e: Exception) {
                onMessageReceived("客户端连接错误: ${e.message}")
        }
    } catch (e: Exception) {
        onMessageReceived("服务器错误: ${e.message}")
    }


}