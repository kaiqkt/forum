package social.media.service.application.dto.request

import social.media.service.domain.entities.ArticleUpdate
import social.media.service.domain.entities.Tag

data class ArticleUpdateRequest(
    var title: String?,
    var body: String?,
    var tags: MutableList<Tag> = mutableListOf()
)

fun ArticleUpdateRequest.toDomain() = ArticleUpdate(
    title = this.title,
    body = this.body,
    tags = this.tags
)