package social.media.service.domain.repositories

import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Flux
import social.media.service.domain.entities.Notification

interface NotificationRepository: ReactiveMongoRepository<Notification, String> {
    fun findAllByToProfileId(profileId: String): Flux<Notification>
}