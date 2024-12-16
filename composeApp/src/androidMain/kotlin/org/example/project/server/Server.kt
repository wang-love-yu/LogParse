package org.example.project.server

import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

actual class Server  {

    private var ip: String
    private var port: Int
    private var mReceivedMessageCallback: ((String) -> Unit)? = null
    actual fun startServer(status:(Boolean,String?)->Unit){
        val clientSocket = Socket(ip,port)
        println("Server started on port $port")

        while (true) {
            // 接收服务端的响应
            val inputStream = clientSocket.getInputStream()
            val reader = inputStream.bufferedReader()
            // 启动一个线程用于接收服务端的消息
            Thread {
                try {
                    while (true) {
                        val response = reader.readLine() // 阻塞等待服务器返回的响应
                        if (response != null) {
                            mReceivedMessageCallback?.invoke(response)
                            println("收到服务端响应：$response")
                        }
                    }
                } catch (e: Exception) {
                    println("接收消息时出错：${e.message}")
                }
            }.start()

        }
    }
    private fun handleClient(clientSocket: Socket) {
        try {
            val input = clientSocket.getInputStream()
            val reader = BufferedReader(InputStreamReader(input))
            val message = reader.readLine()
            println("收到客户端消息：$message")

            val output = clientSocket.getOutputStream()
            val writer = PrintWriter(output, true)
            writer.println("服务器已收到消息: $message")
            mReceivedMessageCallback?.invoke(message)
        } catch (e: Exception) {
            println("处理客户端连接时发生异常：${e.message}")
        } finally {
            clientSocket.close()
            println("客户端连接已关闭")
        }
    }


    actual constructor(ip: String, port: Int) {
        this.ip = ip
        this.port = port
    }

    actual fun receiveMessage(callback: (String) -> Unit) {
        mReceivedMessageCallback = callback
    }
}