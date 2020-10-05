package com.toosafinder.util.messagetemplates

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.util.FileCopyUtils
import java.io.InputStreamReader

@Configuration
internal class EmailMessageTemplatesConfiguration {

    @Bean
    fun loader(): MessageTemplateLoader = MessageTemplateLoader()

    @Bean
    fun resolver(): MessageTemplateResolver = MessageTemplateResolver("{", "}")
}

class MessageTemplateLoader {

    fun loadAsString(templatePath: String): String {
        val resource = ClassPathResource(templatePath)
        resource.inputStream.use { return FileCopyUtils.copyToString(InputStreamReader(it)) }
    }
}

class MessageTemplateResolver(
    private val prefix: String,

    private val postfix: String = ""
) {

    fun resolve(template: String, substitutions: Map<String, String>): String = substitutions
        .entries.fold(template) {
                acc, (argName, value) -> acc.replace(getPlaceholderRegex(argName), value)
        }

    private fun getPlaceholderRegex(argName: String) = prefix + argName + postfix
}