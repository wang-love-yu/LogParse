package org.example.project

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.util.zip.GZIPInputStream
import java.util.zip.InflaterInputStream
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.NoSuchPaddingException
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class LoganParser(//128位ase加密Key
    private val mEncryptKey16: ByteArray, //128位aes加密IV
    private val mEncryptIv16: ByteArray
) {
    private var mDecryptCipher: Cipher? = null

    init {
        initEncrypt()
    }

    private fun initEncrypt() {
        val secretKeySpec = SecretKeySpec(mEncryptKey16, ALGORITHM)
        try {
            mDecryptCipher = Cipher.getInstance(ALGORITHM_TYPE)
            mDecryptCipher!!.init(Cipher.DECRYPT_MODE, secretKeySpec, IvParameterSpec(mEncryptIv16))
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: NoSuchPaddingException) {
            e.printStackTrace()
        } catch (e: InvalidKeyException) {
            e.printStackTrace()
        } catch (e: InvalidAlgorithmParameterException) {
            e.printStackTrace()
        }
    }

    fun parse(`is`: InputStream, os: OutputStream) {
        val bos = ByteArrayOutputStream()
        val buffer = ByteArray(1024)
        var t = 0
        try {
            while ((`is`.read(buffer).also { t = it }) >= 0) {
                bos.write(buffer, 0, t)
                bos.flush()
            }
            val content = bos.toByteArray()
            println("size= " + content.size)
            var a = 0
            var b = 0
            var c = 0
            var d = 0
            var num = 0
            var i = 0
            while (i < content.size) {
                val start = content[i]
                if (start == '\u0001'.code.toByte()) {
                    //System.out.println("\1"+" index= "+i);
                    i++
                    val length = (content[i].toInt() and 0xFF) shl 24 or (
                            (content[i + 1].toInt() and 0xFF) shl 16) or (
                            (content[i + 2].toInt() and 0xFF) shl 8) or
                            (content[i + 3].toInt() and 0xFF)
                    i += 3
                    var type: Int

                    if (length > 0) {
                        num++
                        println("len=$length from i=$i")
                        val temp = i + length + 1
                        if (content.size - i - 1 == length) { //异常
                            a++
                            type = 0
                        } else if (content.size - i - 1 > length && '\u0000'.code.toByte() == content[temp]) {
                            b++
                            type = 1
                        } else if (content.size - i - 1 > length && '\u0001'.code.toByte() == content[temp]) { //异常
                            type = 2
                            c++
                        } else {
                            i -= 4
                            d++
                            i++
                            continue
                        }

                        val dest = ByteArray(length)
                        System.arraycopy(content, i + 1, dest, 0, length)
                        println("渠道的下标:" + dest.size)
                        val uncompressBytesArray = ByteArrayOutputStream()
                        var inflaterOs: InflaterInputStream? = null
                        try {
                            uncompressBytesArray.reset()
                            inflaterOs = GZIPInputStream(
                                CipherInputStream(
                                    ByteArrayInputStream(dest),
                                    mDecryptCipher
                                )
                            )
                            var e = 0


                            while ((inflaterOs.read(buffer).also { e = it }) >= 0) {
                                uncompressBytesArray.write(buffer, 0, e)
                            }
                            println("写入成功")
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        var uncompressByte = uncompressBytesArray.toByteArray()
                        uncompressBytesArray.reset()
                        if (num < 600000) {
                            os.write(uncompressByte)
                        }
                        inflaterOs?.close()
                        i += length
                        if (type == 1) {
                            i++
                        }
                    }
                }
                i++
            }
            println("a = " + a + "b= " + b + " c= " + c + " d= " + d)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    companion object {
        private const val ALGORITHM = "AES"
        private const val ALGORITHM_TYPE = "AES/CBC/NoPadding"
    }
}
