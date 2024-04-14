package org.adesso.shared

interface IJwtProvider {
    fun generateAuthToken(userMail: String, userName: String, objectId: String, roles : Set<JwtProviderRole>): String
    fun decodeJwtWithoutVerify(jwt: String): Map<String, Any>
    fun isJwtExpired(jwt: String): Boolean
    fun isJwtValid(jwt: String): Boolean
}

data class JwtProviderRole(
    val id: String,
    val name: String
)
