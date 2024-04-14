package org.adesso.handlers

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity

interface ILoginHandler {
    fun handle(request: HttpServletRequest) : ResponseEntity<String>
}