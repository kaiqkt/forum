package social.media.service.domain.gateways.messaging

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import social.media.service.domain.entities.Notification

interface QueueService {

    fun sendMessage(receiverId: String, content: Notification, topicName: String): Mono<Void>
    fun receive(receiverId: String, topicName: String): Flux<Notification>
}