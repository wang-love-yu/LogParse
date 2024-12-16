package org.example.project

import NetworkHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Main) }
    
    MaterialTheme {
        when (currentScreen) {
            Screen.Main -> MainScreen(onNavigate = { screen -> currentScreen = screen })
            Screen.FileTransfer -> FileTransferScreen(onBack = { currentScreen = Screen.Main })
            Screen.LogParser -> LogParserScreen(onBack = { currentScreen = Screen.Main })
            // 在这里添加更多功能页面
            Screen.Monitor -> MonitorScreen(onBack = { currentScreen = Screen.Main })
        }
    }
}



enum class Screen {
    Main,
    FileTransfer,
    LogParser,
    // 在这里添加更多功能选项
    //抓包数据
    Monitor
}




