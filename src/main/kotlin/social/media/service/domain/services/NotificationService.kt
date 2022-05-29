package social.media.service.domain.services

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import social.media.service.domain.entities.Notification
import social.media.service.domain.gateways.messaging.QueueService
import social.media.service.domain.repositories.NotificationRepository

@Service
class NotificationService(
    @Value("\${queues.notification}") private var topicName: String,
    private val queueService: QueueService,
    private val notificationRepository: NotificationRepository
) {

    private val log: Logger = LoggerFactory.getLogger(NotificationService::class.java)

    fun sendMessage(notification: Notification): Mono<Void> {

        return notificationRepository.save(notification)
            .doOnNext { log.info("Notification persisted successfully {}", it) }
            .flatMap {
                queueService.sendMessage(notification.toProfileId, notification, topicName)
            }

    }

    fun receive(receiverId: String): Flux<Notification> = queueService.receive(receiverId, topicName)

    fun findAllNotifications(profileId: String) = notificationRepository.findAllByToProfileId(profileId)
}
