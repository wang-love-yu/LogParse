package org.example.project.server

import java.net.Socket

expect class Server(ip: String, port: Int) {
    // 或者如果只需要端口
    // constructor(port: Int)
    
    fun startServer(status:(Boolean,String?)->Unit)

    fun receiveMessage(callback: (String) -> Unit)

}