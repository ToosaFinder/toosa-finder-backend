package com.toosafinder.security.events

import com.toosafinder.security.BaseIntegrationTest
import org.flywaydb.core.Flyway
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.web.server.LocalServerPort

@Disabled
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class EventControllerTest(
    @LocalServerPort
    val port: Int,
    @Autowired
    val flyway: Flyway
): BaseIntegrationTest(port, flyway) {

}