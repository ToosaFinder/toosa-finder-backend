package com.toosafinder.email.service

import org.springframework.mail.MailSender
import org.springframework.mail.SimpleMailMessage
import org.springframework.stereotype.Service


@Service
class EmailService(private val sender: MailSender) {

    fun sendMessage(to: Array<String>, subject: String, body: String) {
        val message = SimpleMailMessage()
        message.apply {
            setTo(*to)
            this.subject = subject
            text = body
        }

        sender.send(message)
    }

    fun sendMessage(to: String, subject: String, body: String) =
        sendMessage(arrayOf(to), subject, body)
}