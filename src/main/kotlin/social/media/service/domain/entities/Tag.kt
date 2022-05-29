package social.media.service.domain.entities

import io.azam.ulidj.ULID
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class Tag(
    @Id
    val id: String = ULID.random(),
    val name: String,
)
