package social.media.service.domain.repositories

import org.slf4j.LoggerFactory
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.TextCriteria
import org.springframework.data.mongodb.core.query.TextQuery
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import social.media.service.domain.entities.Article


@Component
class SearchRepositoryImpl(private val reactiveMongoTemplate: ReactiveMongoTemplate): SearchRepository {

    private val log = LoggerFactory.getLogger(SearchRepositoryImpl::class.java)

    override fun searchByText(text: String): Flux<Article> {
        val criteria = TextCriteria
            .forDefaultLanguage()
            .matchingPhrase(text)

        val query = TextQuery.queryText(criteria).sortByScore()
        return reactiveMongoTemplate.find(query, Article::class.java)
    }
}