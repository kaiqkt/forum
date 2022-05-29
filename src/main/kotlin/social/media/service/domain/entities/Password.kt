package social.media.service.domain.entities

data class Password(
    val hash: String,
    val salt: String
)
