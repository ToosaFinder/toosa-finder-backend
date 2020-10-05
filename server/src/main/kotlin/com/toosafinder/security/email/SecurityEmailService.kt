package com.toosafinder.security.email

import com.toosafinder.email.EmailSendingResult
import com.toosafinder.email.EmailService
import com.toosafinder.logging.LoggerProperty
import com.toosafinder.util.ResourceLoader
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.*

@Service
class SecurityEmailService(
    private val emailService: EmailService,

    @Value("\${email.email-confirmation-url}")
    private val emailConfirmationUrl: String,

    @Value("\${email.password-restore-url}")
    private val passwordRestoreUrl: String,

    @Value("\${email.templates.email-confirmation-template-file-path}")
    private val emailConfirmationTemplateFilePath: String,

    @Value("\${email.templates.password-restore-template-file-path}")
    private val passwordRestoreTemplateFilePath: String
) {

    private val templateResolver = MessageTemplateResolver("{", "}")

    private val log by LoggerProperty()

    fun sendEmailConfirmation(email: String, uuid: UUID): EmailSendingResult {
        log.trace("Trying to send email confirmation message to $email")

        val template = ResourceLoader.loadAsString(emailConfirmationTemplateFilePath)
        val substitutions = mapOf(
            "url" to emailConfirmationUrl, "name" to email, "uuid" to uuid.toString()
        )
        val body = templateResolver.resolve(template, substitutions)

        return emailService.sendMessage(email, "Подтверждение почты", body)
    }

    fun sendPasswordRestore(email: String, uuid: UUID): EmailSendingResult {
        log.trace("Trying to send password restore message to $email")

        val template = ResourceLoader.loadAsString(passwordRestoreTemplateFilePath)
        val substitutions = mapOf(
            "url" to passwordRestoreUrl, "name" to email, "uuid" to uuid.toString()
        )
        val body = templateResolver.resolve(template, substitutions)

        return emailService.sendMessage(email, "Сброс пароля", body)
    }
}

internal class MessageTemplateResolver(
    private val prefix: String,

    private val postfix: String = ""
) {

    fun resolve(template: String, substitutions: Map<String, String>): String =
        substitutions.entries.fold(template) {
                acc, (argName, value) -> acc.replace(getPlaceholderRegex(argName), value)
        }
    private fun getPlaceholderRegex(argName: String) = prefix + argName + postfix
}