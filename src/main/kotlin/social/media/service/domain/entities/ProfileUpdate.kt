package social.media.service.domain.entities

data class ProfileUpdate(
    val profileId: String,
    var about: String?,
    var image: String?
)
