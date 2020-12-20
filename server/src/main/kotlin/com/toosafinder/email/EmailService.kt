package com.toosafinder.email

import org.springframework.mail.MailSendException
import org.springframework.mail.MailSender
import org.springframework.mail.SimpleMailMessage
import org.springframework.stereotype.Service


@Service
class EmailService(private val sender: MailSender) {

    fun sendMessage(to: Array<String>, subject: String, body: String): EmailSendingResult {
        val message = SimpleMailMessage()
        message.apply {
            setTo(*to)
            this.subject = subject
            text = body
        }

        return try {
            sender.send(message)
            EmailSendingResult.Success
        } catch (e: MailSendException) {
            EmailSendingResult.SendingError
        }
    }

    fun sendMessage(to: String, subject: String, body: String) =
        sendMessage(arrayOf(to), subject, body)
}

sealed class EmailSendingResult {

    object Success: EmailSendingResult()

    object SendingError: EmailSendingResult()
}