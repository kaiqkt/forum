package social.media.service.domain.repositories

import reactor.core.publisher.Flux
import social.media.service.domain.entities.Article

interface SearchRepository {

    fun searchByText(text: String): Flux<Article>
}