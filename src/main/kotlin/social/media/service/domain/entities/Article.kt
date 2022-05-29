package social.media.service.domain.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import io.azam.ulidj.ULID
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.TextIndexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.TextScore
import java.time.LocalDateTime

@Document
data class Article(
    @Id
    val id: String = ULID.random(),
    val profileId: String,
    @TextIndexed(weight = 4F)
    var username: String,
    @TextIndexed(weight = 3F)
    var title: String,
    @TextIndexed(weight = 2F)
    var body: String,
    @TextIndexed(weight = 1F)
    var tags: MutableList<Tag>,
    var comments: MutableList<Comment> = mutableListOf(),
    var likes: MutableList<String> = mutableListOf(),
    var createdAt: LocalDateTime = LocalDateTime.now(),
    var updatedAt: LocalDateTime = LocalDateTime.now(),
    @JsonIgnore
    @TextScore
    var score: Float? = null
) {
    fun likesCount() = likes.size
}
