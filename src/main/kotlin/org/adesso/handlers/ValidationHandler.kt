package org.adesso.handlers

import jakarta.servlet.http.HttpServletRequest
import org.adesso.configs.JwtProperties
import org.adesso.shared.IJwtProvider
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class ValidationHandler(
    private val jwtProperties: JwtProperties,
    private val jwtProvider: IJwtProvider,
    private val tokenCache: MutableSet<String>
) : IValidateHandler {
    private val logger = LoggerFactory.getLogger(
        ValidationHandler::class.java,
    )

    override fun handle(request: HttpServletRequest): ResponseEntity<String> {
        val token = extractToken(request)

        return when {
            token == null -> {
                val message = "No token found in request."
                logger.info(message)
                unauthorized(message)
            }
            tokenCache.contains(token) -> validateCachedToken(token)
            else -> validateNewToken(token)
        }
    }

    private fun extractToken(request: HttpServletRequest): String? =
        request.cookies?.firstOrNull { it.name == jwtProperties.cookieName }?.value

    private fun validateCachedToken(token: String): ResponseEntity<String> {
        val email = jwtProvider.decodeJwtWithoutVerify(token)["email"]
        return if (email == null) {
            tokenCache.remove(token)
            val message = "Cached Token has no email claim, removing it from cache."
            logger.warn(message)
            unauthorized(message)
        } else {
            logger.info("Cached token valid for user '$email'.")
            ResponseEntity.ok("Token is cached and valid.")
        }
    }

    private fun validateNewToken(token: String): ResponseEntity<String> {
        return if (jwtProvider.isJwtValid(token)) {
            val email = jwtProvider.decodeJwtWithoutVerify(token)["email"]
            tokenCache.add(token)
            logger.info("New token for user '$email' added to cache.")
            ResponseEntity.ok("Token is valid.")
        } else {
            val message = "Invalid Token."
            logger.warn(message)
            unauthorized(message)
        }
    }

    private fun unauthorized(message: String): ResponseEntity<String> =
        ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(message)
}