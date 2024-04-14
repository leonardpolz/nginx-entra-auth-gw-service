package org.adesso.controllers

import jakarta.servlet.http.HttpServletRequest
import org.adesso.handlers.ICallbackHandler
import org.adesso.handlers.ILoginHandler
import org.adesso.handlers.IValidateHandler
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/")
class AuthController(
    private val loginHandler: ILoginHandler,
    private val callbackHandler: ICallbackHandler,
    private val validateHandler: IValidateHandler,
) {
    private val logger = LoggerFactory.getLogger(AuthController::class.java)

    @GetMapping("/login")
    fun login(request: HttpServletRequest): ResponseEntity<String> {
        logger.debug("Processing login request from ${request.remoteAddr}")
        return loginHandler.handle(request).also {
            logger.info("Login request processed")
        }
    }

    @PostMapping("/callback")
    fun callback(request: HttpServletRequest): ResponseEntity<String> {
        logger.debug("Processing callback request from ${request.remoteAddr}")
        return callbackHandler.handle(request).also {
            logger.info("Callback request processed")
        }
    }

    @GetMapping("/validate")
    fun validateToken(request: HttpServletRequest): ResponseEntity<String> {
        logger.debug("Processing token validation request from ${request.remoteAddr}")
        return validateHandler.handle(request).also {
            logger.info("Validate request processed")
        }
    }
}