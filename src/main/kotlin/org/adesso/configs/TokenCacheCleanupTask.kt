package org.adesso.configs

import org.adesso.shared.IJwtProvider
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class TokenCacheCleanupTask(
    private val tokenCache: MutableSet<String>,
    private val jwtProvider: IJwtProvider
) {
    private val logger = LoggerFactory.getLogger(TokenCacheCleanupTask::class.java,)
    @Scheduled(fixedRate = 60000)
    fun cleanupTokenCache() {
        tokenCache.removeIf { !jwtProvider.isJwtValid(it) }
        val iterator = tokenCache.iterator()
        while (iterator.hasNext()) {
            val token = iterator.next()
            if (jwtProvider.isJwtExpired(token)) {
                iterator.remove()
            }
        }

        logger.info("Token cache cleanup finished, {} tokens remaining", tokenCache.size)
    }
}