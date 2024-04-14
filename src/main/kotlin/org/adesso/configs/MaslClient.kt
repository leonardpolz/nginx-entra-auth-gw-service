package org.adesso.org.adesso.configs

import com.microsoft.aad.msal4j.ClientCredentialFactory
import com.microsoft.aad.msal4j.ConfidentialClientApplication
import org.adesso.configs.EntraIdProperties
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MaslClient(private val entraIdProperties: EntraIdProperties) {
    private val logger = LoggerFactory.getLogger(MaslClient::class.java)

    @Bean
    fun confidentialClientApplication(): ConfidentialClientApplication {
        return try {
            ConfidentialClientApplication.builder(
                entraIdProperties.clientId,
                ClientCredentialFactory.createFromSecret(entraIdProperties.clientSecret)
            )
                .authority(entraIdProperties.authority)
                .build().also {
                    logger.info("ConfidentialClientApplication created successfully.")
                }
        } catch (e: Exception) {
            logger.error("Failed to create ConfidentialClientApplication", e)
            throw e
        }
    }
}