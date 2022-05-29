package social.media.service.application.dto.response

import social.media.service.domain.entities.Profile
import java.time.LocalDateTime

data class ProfileResponse(
    val id: String,
    val name: String,
    var username: String,
    var about: String,
    var image: String?,
    var following: Int,
    var followers: Int,
    var createdAt: LocalDateTime
)

fun Profile.toResponse() = ProfileResponse(
    id = this.id,
    name = this.name,
    username = this.username,
    about = this.about,
    image = this.image,
    followers = this.followersCount(),
    following = this.followingCount(),
    createdAt = this.createdAt
)