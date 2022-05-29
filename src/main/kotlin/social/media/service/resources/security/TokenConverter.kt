package social.media.service.resources.security

import org.springframework.http.HttpHeaders
import org.springframework.security.core.Authentication
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import social.media.service.domain.utils.JWTUtil

@Component
class TokenConverter(private val tokenProvider: JWTUtil) : ServerAuthenticationConverter {

    private fun resolveToken(exchange: ServerWebExchange): Mono<String> {
        return Mono.justOrEmpty(exchange.request.headers.getFirst(HttpHeaders.AUTHORIZATION))
            .filter { t -> t.startsWith("Bearer ") }
            .map { t -> t.substring(7) }
    }

    override fun convert(exchange: ServerWebExchange): Mono<Authentication> {
        return resolveToken(exchange)
            .filter(tokenProvider::validToken)
            .map(tokenProvider::getAuthentication)
    }

}