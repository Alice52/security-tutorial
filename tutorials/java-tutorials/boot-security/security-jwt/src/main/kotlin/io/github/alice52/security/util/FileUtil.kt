package io.github.alice52.security.util

import org.springframework.core.io.ClassPathResource
import org.springframework.util.FileCopyUtils.copyToByteArray
import java.io.File
import java.io.IOException
import java.nio.file.Files

object FileUtil {

    @Throws(Exception::class)
    fun readFile(fileName: String): ByteArray {
        val resource = ClassPathResource(fileName);
        return copyToByteArray(resource.inputStream)
    }


    @Throws(IOException::class)
    fun writeFile(destPath: String, bytes: ByteArray) {

        val dest = File(destPath)
        if (!dest.exists()) {
            dest.createNewFile()
        }
        Files.write(dest.toPath(), bytes)
        /*
                val resource = ClassPathResource(destPath)
                if (!resource.exists()) {
                    resource.createRelative(destPath);
                }

                val file = resource.file
                val writer = FileWriter(file)
                writer.write(String(bytes))
                writer.close()
        */
    }
}