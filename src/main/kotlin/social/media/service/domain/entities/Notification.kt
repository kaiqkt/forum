package social.media.service.domain.entities

import io.azam.ulidj.ULID
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document
data class Notification(
    @Id
    val id: String = ULID.random(),
    val fromProfileId: String,
    val toProfileId: String,
    val notificationType: NotificationType,
    var createdAt: LocalDateTime = LocalDateTime.now()
)
