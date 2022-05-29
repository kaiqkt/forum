package social.media.service.application.dto.response

import social.media.service.domain.entities.Article
import social.media.service.domain.entities.Comment
import social.media.service.domain.entities.Tag
import java.time.LocalDateTime

data class ArticleResponse(
    val id: String,
    val profileId: String,
    var username: String,
    var title: String,
    var body: String,
    var tags: MutableList<Tag>,
    var comments: MutableList<Comment>,
    var likes: Int,
    var createdAt: LocalDateTime
)

fun Article.toResponse() = ArticleResponse(
    id = this.id,
    profileId = this.profileId,
    username = this.username,
    title = this.title,
    body = this.body,
    tags = this.tags,
    comments = this.comments,
    likes = this.likesCount(),
    createdAt = this.createdAt
)
