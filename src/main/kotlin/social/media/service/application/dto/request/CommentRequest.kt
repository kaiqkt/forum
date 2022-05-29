package social.media.service.application.dto.request

import io.azam.ulidj.ULID
import social.media.service.domain.entities.Comment
import javax.validation.constraints.NotBlank

data class CommentRequest(
    @get:NotBlank(message = "body cannot be empty.")
    var body: String
)

fun CommentRequest.toDomain(articleId: String, profileId: String) = Comment(
    id = "${articleId}-${ULID.random().substring(0, 8)}",
    body = this.body,
    profileId = profileId
)