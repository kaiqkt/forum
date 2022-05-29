package social.media.service.domain.entities

import java.time.LocalDateTime

data class Comment(
    val id: String,
    var body: String,
    var profileId: String,
    var createdAt: LocalDateTime = LocalDateTime.now()
)
