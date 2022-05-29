package social.media.service.application.dto.request

import social.media.service.domain.entities.Article
import social.media.service.domain.entities.Tag
import javax.validation.constraints.NotBlank

data class ArticleRequest(
    var username: String,
    @get:NotBlank(message = "title cannot be empty.")
    var title: String,
    @get:NotBlank(message = "body cannot be empty.")
    var body: String,
    var tags: MutableList<@NotBlank(message = "tag cannot be empty.") Tag>
)

fun ArticleRequest.toDomain(profileId: String) = Article(
    profileId = profileId,
    username = this.username,
    title = this.title,
    body = this.body,
    tags = this.tags
)

