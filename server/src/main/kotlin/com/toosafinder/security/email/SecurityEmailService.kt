package com.toosafinder.security.email

import com.toosafinder.email.service.EmailService
import com.toosafinder.util.messagetemplates.MessageTemplateLoader
import com.toosafinder.util.messagetemplates.MessageTemplateResolver
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.*

@Service
class SecurityEmailService(
    private val emailService: EmailService,

    private val templateLoader: MessageTemplateLoader,

    private val templateResolver: MessageTemplateResolver,

    @Value("\${email.email-confirmation-url}")
    private val emailConfirmationUrl: String,

    @Value("\${email.password-restore-url}")
    private val passwordRestoreUrl: String,

    @Value("\${email.templates.email-confirmation-template-file-path}")
    private val emailConfirmationTemplateFilePath: String,

    @Value("\${email.templates.password-restore-template-file-path}")
    private val passwordRestoreTemplateFilePath: String
) {

    fun sendEmailConfirmation(email: String, uuid: UUID) {
        val template = templateLoader.loadAsString(emailConfirmationTemplateFilePath)
        val substitutions = mapOf<String, String>(
            "url" to emailConfirmationUrl, "name" to email, "uuid" to uuid.toString()
        )
        val body = templateResolver.resolve(template, substitutions)

        emailService.sendMessage(email, "Подтверждение почты", body)
    }

    fun sendPasswordRestore(email: String, uuid: UUID) {
        val template = templateLoader.loadAsString(passwordRestoreTemplateFilePath)
        val substitutions = mapOf<String, String>(
            "url" to passwordRestoreUrl, "name" to email, "uuid" to uuid.toString()
        )
        val body = templateResolver.resolve(template, substitutions)

        emailService.sendMessage(email, "Сброс пароля", body)
    }
}