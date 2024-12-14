package org.example.project

expect  class OnlyFileSelector() {

    fun chooseFilePath(onFinish: (String) -> Unit)
}