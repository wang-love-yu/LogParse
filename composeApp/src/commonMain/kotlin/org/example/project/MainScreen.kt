package org.example.project

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview()
@Composable
fun MainScreen(onNavigate: (Screen) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = { onNavigate(Screen.FileTransfer) }) {
            Text("文件传输1")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { onNavigate(Screen.LogParser) }) {
            Text("日志解析")
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { onNavigate(Screen.Monitor) }) {
            Text("抓包数据")
        }
        // 在这里添加更多功能按钮
    }
}