package social.media.service.application.dto.request

import social.media.service.domain.entities.Profile
import social.media.service.domain.utils.PasswordUtils
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotEmpty

data class ProfileRequest(
    @get:NotBlank(message = "name cannot be empty.")
    val name: String,
    @get:NotEmpty(message = "username cannot be empty.")
    val username: String,
    @get:NotBlank(message = "email cannot be empty.")
    val email: String,
    @get:NotBlank(message = "password cannot be empty.")
    val password: String,
    @get:NotBlank(message = "about cannot be empty.")
    val about: String,
    val image: String?
)

fun ProfileRequest.toDomain(profileId: String) = Profile(
    id = profileId,
    name = this.name,
    username = this.username,
    email = this.email,
    password = PasswordUtils.encryptPassword(password),
    about = this.about,
    image = this.image,
)