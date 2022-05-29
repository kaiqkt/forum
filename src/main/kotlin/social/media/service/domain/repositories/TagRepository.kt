package social.media.service.domain.repositories

import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import social.media.service.domain.entities.Tag

@Repository
interface TagRepository: ReactiveMongoRepository<Tag, String> {
}