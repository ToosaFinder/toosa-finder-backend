package com.toosafinder.security.registration

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.toosafinder.api.UserCredentials
import io.kotest.matchers.shouldBe
import io.restassured.RestAssured
import io.restassured.filter.log.RequestLoggingFilter
import io.restassured.filter.log.ResponseLoggingFilter
import io.restassured.http.ContentType
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.flywaydb.core.Flyway
import org.junit.Ignore
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort

//этот тэг нужен чтобы можно было запустить юнит-тесты отдельно
@Tag("integrationTest")
@Disabled
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
internal class RegistrationControllerTest(
    @LocalServerPort
    val port: Int,
    @Autowired
    val flyway: Flyway
) {

    init {
        //чистим базу и накатываем миграции для тестов.
        //неужели для каждого теста придется делать так?..
        //если кто придумает что получше, прошу предлагайте
        flyway.clean()
        flyway.migrate()
        RestAssured.port = port
    }

    private val serializer = ObjectMapper()

    private val user1 = UserCredentials(
        email = "email@example.com",
        password = "password"
    )

    private val duplicatedUser = UserCredentials(
        email = "email2@example.com",
        password = "password"
    )

    @Test
    @Order(1)
    fun `should register new user successfully`() {
        println("hello 1")
        Given {
            body(serializer.writeValueAsString(user1))
            contentType(ContentType.JSON)
        } When {
            put("/user/registration")
        } Then {
            statusCode(200)
        }
    }

    @Test
    @Order(2)
    fun `should prohibit registration because of user duplication`(){
        println("hello 2")
        Given {
            body(serializer.writeValueAsString(duplicatedUser))
            contentType(ContentType.JSON)
            filter(RequestLoggingFilter())
        }When {
            put("/user/registration")
        } Then {
            statusCode(200)
        }

        Given {
            body(serializer.writeValueAsString(duplicatedUser))
            contentType(ContentType.JSON)
        } When {
            put("/user/registration")
        } Then {
            statusCode(409)
        }
    }

    @Test
    @Order(3)
    fun `should return all registered users`(){
        // почему-то возвращает пустой список, хоть и выполняется этот метод последним
        println("hello 3")
        Given {
            //в консольке можно увидеть что тело ответа пустое
            filter(ResponseLoggingFilter())
        } When {
            get("/user/registration")
        } Then {
            statusCode(200)
            serializer.readValue<List<UserCredentials>>(
                extract().body().asString()
            ).toSet() shouldBe setOf(user1, duplicatedUser)
        }
    }

}