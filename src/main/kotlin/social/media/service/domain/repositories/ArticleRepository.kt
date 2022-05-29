package social.media.service.domain.repositories

import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.data.mongodb.repository.Tailable
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import social.media.service.domain.entities.Article

@Repository
interface ArticleRepository: ReactiveMongoRepository<Article, String>{
    fun findByProfileIdInOrderByCreatedAtAsc(ids: List<String>): Flux<Article>
    @Tailable
    fun findWithTailableCursorByProfileId(ids: List<String>): Flux<Article>
}