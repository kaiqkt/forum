package social.media.service.domain.entities

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime
import javax.validation.constraints.Email

@Document
data class Profile(
    @Id
    val id: String,
    val name: String,
    val username: String,
    val email: String,
    val password: Password,
    var about: String,
    var image: String? = null,
    val following: MutableList<String> = mutableListOf(),
    val followers: MutableList<String> = mutableListOf(),
    val createdAt: LocalDateTime = LocalDateTime.now(),
    var updatedAt: LocalDateTime = LocalDateTime.now()
){
    fun followingCount() = following.size
    fun followersCount() = followers.size
}
