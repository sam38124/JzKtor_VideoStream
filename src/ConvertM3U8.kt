import java.io.File
import java.io.IOException
import java.io.InputStream
import java.util.*
import kotlin.collections.ArrayList

object ConvertM3U8 {

    var fileIndex=0
    fun convertOss(fil: ByteArray,toFile:String): Boolean {
        fileIndex+=1
        File("Video_Convert_Root/$fileIndex").createNewFile()
        val file=File("Video_Convert_Root/$fileIndex")
        file.writeBytes(fil)
        return processM3U8((file),toFile)
    }

    // ffmpeg能解析的格式：（asx，asf，mpg，wmv，3gp，mp4，mov，avi，flv等）
    private fun processM3U8(file: File,oFile:String): Boolean {
        //这里就写入执行语句就可以了
        val commend: ArrayList<String> = ArrayList<String>()
        commend.add("/usr/local/bin/ffmpeg")
        commend.add("-i")
        commend.add(file.absolutePath)
        commend.add("-c:v")
        commend.add("libx264")
        commend.add("-hls_time")
        commend.add("20")
        commend.add("-hls_list_size")
        commend.add("0")
        commend.add("-c:a")
        commend.add("aac")
        commend.add("-strict")
        commend.add("-2")
        commend.add("-f")
        commend.add("hls")
        commend.add("HLS_ROUT/$oFile/root.m3u8")
        return try {
            File("HLS_ROUT/$oFile").mkdir()
            val builder = ProcessBuilder(commend.toList()) //java
            val p: Process = builder.start()
            val i = doWaitFor(p)
            println("------>$i")
            p.destroy()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 监听ffmpeg运行过程
     * @param p
     * @return
     */
    fun doWaitFor(p: Process): Int {
        var `in`: InputStream? = null
        var err: InputStream? = null
        var exitValue = -1 // returned to caller when p is finished
        try {
            println("comeing")
            `in` = p.inputStream
            err = p.errorStream
            var finished = false // Set to true when p is finished
            while (!finished) {
                try {
                    while (`in`.available() > 0) {
                        val c = `in`.read().toChar()
                        print(c)
                    }
                    while (err.available() > 0) {
                        val c = err.read().toChar()
                        print(c)
                    }
                    exitValue = p.exitValue()
                    finished = true
                } catch (e: IllegalThreadStateException) {
                    Thread.sleep(500)
                }
            }
        } catch (e: Exception) {
            System.err.println(
                "doWaitFor();: unexpected exception - "
                        + e.message
            )
        } finally {
            try {
                `in`?.close()
            } catch (e: IOException) {
                println(e.message)
            }
            if (err != null) {
                try {
                    err.close()
                } catch (e: IOException) {
                    println(e.message)
                }
            }
        }
        return exitValue
    }
}