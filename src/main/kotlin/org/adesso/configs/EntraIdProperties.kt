package org.adesso.configs

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "entraid")
data class EntraIdProperties(
    val clientId: String,
    val tenantId: String,
    val scopes: Set<String>,
    val authority: String,
    val redirectUri: String,
    val clientSecret: String
)