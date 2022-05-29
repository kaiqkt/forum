package social.media.service.domain.entities

import javax.validation.constraints.NotBlank

data class Login(
    @NotBlank(message = "email cannot be empty.")
    val email: String,
    @NotBlank(message = "password cannot be empty.")
    val password: String
)
