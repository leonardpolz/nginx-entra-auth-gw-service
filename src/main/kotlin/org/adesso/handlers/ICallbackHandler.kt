package org.adesso.handlers

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity

interface ICallbackHandler {
    fun handle(request: HttpServletRequest) : ResponseEntity<String>
}