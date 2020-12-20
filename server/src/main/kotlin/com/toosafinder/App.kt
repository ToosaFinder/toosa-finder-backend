package com.toosafinder

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration

@SpringBootApplication(
    exclude = [SecurityAutoConfiguration::class]
)
class App

fun main() {
    SpringApplication.run(App::class.java)
}