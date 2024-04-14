package org.adesso.configs

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "jwt")
data class JwtProperties (
    val secret: String,
    val issuer: String,
    val expirationTime: Int,
    val cookieName: String,
)