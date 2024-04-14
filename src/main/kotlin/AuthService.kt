package org.adesso

import org.adesso.configs.EntraIdProperties
import org.adesso.configs.JwtProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(EntraIdProperties::class, JwtProperties::class)
class AuthService

fun main(args: Array<String>) {
    runApplication<AuthService>(*args)
}
