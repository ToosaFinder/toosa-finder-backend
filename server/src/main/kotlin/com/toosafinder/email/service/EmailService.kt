package com.toosafinder.email.service

import org.springframework.mail.MailSender
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service


@Service
class EmailService(private val sender: MailSender) {
    fun sendMessage(to: Array<String>, subject: String, text: String) {
        val message = SimpleMailMessage()
        message.setTo(*to)
        message.subject = subject
        message.text = text

        sender.send(message)
    }

    fun sendMessage(to: String, subject: String, text: String) = sendMessage(
        arrayOf(to), subject, text
    )
}