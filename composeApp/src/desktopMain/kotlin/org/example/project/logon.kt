import org.example.project.LoganParser
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream





fun main() {
    //ase加密Key
    val encryptKey16 = "0123456789012345"

    var a  = ""
    //aes加密IV
    val encryptIv16 = "0123456789012345"
    //原始文件地址
    val filePath = "/Users/leiwang/Downloads/untitled4/logan/1a6d7c393fe152d94d.1733328000000"
    val inputFile = File(filePath)
    val uncompressBytesArray = ByteArrayOutputStream()
    val loganParser = LoganParser(encryptKey16.toByteArray(), encryptIv16.toByteArray())
    //loganParser.parse()
    //输出文件地址eer人`
    val outPutFile = File("/Users/leiwang/Downloads/untitled4/out/561.log")

    if (!outPutFile.exists()) {
        outPutFile.createNewFile()
    }
    loganParser.parse(FileInputStream(inputFile), uncompressBytesArray)
    outPutFile.writeBytes(uncompressBytesArray.toByteArray())

}