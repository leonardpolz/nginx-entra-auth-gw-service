package org.adesso.shared

import com.auth0.jwt.JWT
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.adesso.configs.JwtProperties
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.*

@Service
class JwtProvider(private val jwtProperties: JwtProperties) : IJwtProvider {

    private val logger = LoggerFactory.getLogger(JwtProvider::class.java)
    override fun generateAuthToken(userMail: String, userName: String, objectId: String, roles: Set<JwtProviderRole>): String {
        val nowMillis = System.currentTimeMillis()
        val key = Keys.hmacShaKeyFor(jwtProperties.secret.toByteArray())

        return Jwts.builder()
            .claim("oid", objectId)
            .claim("email", userMail)
            .claim("name", userName)
            .claim("roles", roles)
            .issuer(jwtProperties.issuer)
            .issuedAt(Date(nowMillis))
            .expiration(Date(nowMillis + jwtProperties.expirationTime))
            .signWith(key)
            .compact()
            .also { logger.debug("Generated JWT token for user: {}", userMail) }
    }

    override fun decodeJwtWithoutVerify(jwt: String): Map<String, Any> =
        JWT.decode(jwt).claims.mapValues { it.value.asString()?.replace("\"", "").toString() }

    override fun isJwtExpired(jwt: String): Boolean {
        val claims = JWT.decode(jwt).claims
        val expiration = Date(claims["exp"]?.asLong() ?: 0)
        return expiration.before(Date())
    }

    override fun isJwtValid(jwt: String): Boolean =
        try {
            val claims = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(jwtProperties.secret.toByteArray()))
                .build()
                .parseSignedClaims(jwt)
                .payload

            val mailClaim = claims["email"]

            when {
                mailClaim == null -> {
                    logger.error("JWT token has no email claim.")
                    false
                }
                claims.expiration.before(Date()) -> {
                    logger.info("JWT token for user '$mailClaim' is expired.")
                    false
                }

                claims.issuer != jwtProperties.issuer -> {
                    logger.info("JWT token for user '$mailClaim' has incorrect issuer.")
                    false
                }

                else -> {
                    logger.info("JWT token for user '$mailClaim' is valid.")
                    true
                }
            }
        } catch (e: Exception) {
            logger.error("Error validating JWT token", e)
            false
        }
}