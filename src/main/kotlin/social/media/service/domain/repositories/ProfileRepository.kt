package social.media.service.domain.repositories

import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import social.media.service.domain.entities.Profile

@Repository
interface ProfileRepository: ReactiveMongoRepository<Profile, String> {
    fun existsByUsername(username: String): Mono<Boolean>
    fun existsByEmail(email: String): Mono<Boolean>
    fun findByEmail(email: String): Mono<Profile>
    fun findByUsername(username: String): Mono<Profile?>
}