package org.adesso.handlers

import com.microsoft.aad.msal4j.AuthorizationRequestUrlParameters
import com.microsoft.aad.msal4j.ConfidentialClientApplication
import jakarta.servlet.http.HttpServletRequest
import org.adesso.configs.EntraIdProperties
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.net.URI

@Service
class LoginHandler(
    private val entraIdProperties: EntraIdProperties,
    private val msalClient: ConfidentialClientApplication
) : ILoginHandler {
    private val logger = LoggerFactory.getLogger(LoginHandler::class.java)

    override fun handle(request: HttpServletRequest): ResponseEntity<String> {
        val state = extractRedirectState(request)
        val parameters = buildAuthorizationRequestParameters(state)
        val authorizationRequestUrl = msalClient.getAuthorizationRequestUrl(parameters).toString()

        logger.info("Redirecting to authorization endpoint, state parameter: {}",state)
        return ResponseEntity.status(HttpStatus.SEE_OTHER)
            .location(URI.create(authorizationRequestUrl))
            .build()
    }

    private fun extractRedirectState(request: HttpServletRequest): String =
        request.getParameter("rd") ?: throw IllegalArgumentException("No redirect parameter found")

    private fun buildAuthorizationRequestParameters(state: String): AuthorizationRequestUrlParameters =
        AuthorizationRequestUrlParameters
            .builder(entraIdProperties.redirectUri, entraIdProperties.scopes)
            .state(state)
            .build()
}