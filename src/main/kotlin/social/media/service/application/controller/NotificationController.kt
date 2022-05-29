package social.media.service.application.controller

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import social.media.service.domain.entities.Notification
import social.media.service.domain.services.NotificationService

@RestController
@RequestMapping("/notification")
class NotificationController(private val notificationService: NotificationService) {

    @GetMapping(value = ["/{profileId}/stream"], produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun notification(@PathVariable profileId: String): Flux<Notification> {
        return notificationService.receive(profileId)
    }

    @GetMapping("/{profileId}")
    fun allNotifications(@PathVariable profileId: String): Flux<Notification> {
        return notificationService.findAllNotifications(profileId)
    }
}