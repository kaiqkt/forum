package social.media.service.domain.services

import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import social.media.service.domain.entities.Login
import social.media.service.domain.exceptions.AuthorizationException
import social.media.service.domain.repositories.ProfileRepository
import social.media.service.domain.utils.JWTUtil
import social.media.service.domain.utils.PasswordUtils

@Service
class AuthorizationService(private val profileRepository: ProfileRepository, private val jwtUtil: JWTUtil) {

    fun authorization(login: Login): Mono<String> {
        return profileRepository.findByEmail(login.email)
            .filter { it != null }
            .flatMap {
                if (PasswordUtils.validatePassword(login.password, it.password)) {
                    val jwt = jwtUtil.generateToken(it.id)
                    return@flatMap Mono.just(jwt)
                }

                Mono.empty()
            }
            .switchIfEmpty { throw AuthorizationException("Invalid email or password") }
    }
}