package org.adesso.handlers

import com.microsoft.aad.msal4j.AuthorizationCodeParameters
import com.microsoft.aad.msal4j.ConfidentialClientApplication
import jakarta.servlet.http.HttpServletRequest
import org.adesso.configs.EntraIdProperties
import org.adesso.configs.IJwtProviderRoleMapper
import org.adesso.configs.JwtProperties
import org.adesso.repositories.IUserRepository
import org.adesso.shared.IJwtProvider
import org.adesso.shared.JwtProviderRole
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.net.URI
import java.util.*

@Service
class CallbackHandler(
    private val maslClient: ConfidentialClientApplication,
    private val entraIdProperties: EntraIdProperties,
    private val jwtProperties: JwtProperties,
    private val jwtProvider: IJwtProvider,
    private val userRepository: IUserRepository,
    private val tokenCache: MutableSet<String>
) : ICallbackHandler {
    private val logger = LoggerFactory.getLogger(CallbackHandler::class.java)

    override fun handle(request: HttpServletRequest): ResponseEntity<String> {
        val parameters = createAuthorizationCodeParameters(request)
        val idToken = getIdToken(parameters)
        val idClaims = jwtProvider.decodeJwtWithoutVerify(idToken)

        logger.info("Received callback from Entra ID for user '{}'", idClaims["preferred_username"]
            ?: {
                logger.error("No username found in idToken")
                throw IllegalStateException("No username found in idToken")
            })

        val email = idClaims["preferred_username"] as String
        val name = idClaims["name"] as String
        val objectId = idClaims["oid"] as String
        val user = userRepository.findById(UUID.fromString(objectId)).orElse(null)
        var roles = emptySet<JwtProviderRole>()
        if (user != null) {
            roles = IJwtProviderRoleMapper.instance.toJwtProviderRoles(user.roles)
        } else {
            logger.warn("User with object_id '{}' (email: '{}') was not found in the database.", objectId, email)
        }

        val authToken = jwtProvider.generateAuthToken(
            email,
            name,
            objectId,
            roles,
        )

        tokenCache.add(authToken)
        logger.info("Token for user '{}' added to cache", email)

        val cookieValue = formatCookieValue(authToken)
        val state = request.getParameter("state") ?: "/"
        logger.info("Redirecting to '{}'", state)

        return buildRedirectResponse(state, cookieValue)
    }

    private fun createAuthorizationCodeParameters(request: HttpServletRequest): AuthorizationCodeParameters =
        AuthorizationCodeParameters.builder(
            request.getParameter("code"),
            URI(entraIdProperties.redirectUri)
        ).scopes(entraIdProperties.scopes).build()


    private fun getIdToken(parameters: AuthorizationCodeParameters): String =
        try {
            maslClient.acquireToken(parameters).get().idToken().also {
                logger.info("IdToken acquired")
            }
        } catch (e: Exception) {
            logger.error("Error acquiring token", e)
            throw e
        }

    private fun formatCookieValue(authToken: String): String =
        "${jwtProperties.cookieName}=${authToken.replace("\"", "\\\"")}"

    private fun buildRedirectResponse(state: String, cookieValue: String): ResponseEntity<String> =
        ResponseEntity.status(HttpStatus.SEE_OTHER).headers(HttpHeaders().apply {
            add("Set-Cookie", cookieValue)
            location = URI.create(state)
        }).build()
}