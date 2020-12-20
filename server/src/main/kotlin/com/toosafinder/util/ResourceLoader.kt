package com.toosafinder.util

import org.springframework.core.io.ClassPathResource
import org.springframework.util.FileCopyUtils
import java.io.InputStreamReader

class ResourceLoader {

    companion object {
        fun loadAsString(templatePath: String): String {
            val resource = ClassPathResource(templatePath)
            resource.inputStream.use { return FileCopyUtils.copyToString(InputStreamReader(it)) }
        }
    }
}