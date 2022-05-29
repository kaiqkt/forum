package social.media.service.application.dto.request

import social.media.service.domain.entities.ProfileUpdate

data class ProfileUpdateRequest(
    var about: String?,
    var image: String?
)

fun ProfileUpdateRequest.toDomain(profileId: String) = ProfileUpdate(
    profileId = profileId,
    about = this.about,
    image = this.image
)