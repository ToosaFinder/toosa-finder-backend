package com.toosafinder.security.login

import com.toosafinder.security.BaseIntegrationTest
import org.flywaydb.core.Flyway
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.web.server.LocalServerPort

@Disabled
internal class LoginControllerTest(
    @LocalServerPort
    val port: Int,
    @Autowired
    val flyway: Flyway
): BaseIntegrationTest(port, flyway) {


}