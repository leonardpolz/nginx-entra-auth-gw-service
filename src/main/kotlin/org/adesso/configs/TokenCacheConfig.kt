package org.adesso.configs

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.ConcurrentHashMap

@Configuration
class TokenCacheConfig {
    @Bean
    fun tokenCache(): MutableSet<String> {
        return ConcurrentHashMap.newKeySet()
    }
}