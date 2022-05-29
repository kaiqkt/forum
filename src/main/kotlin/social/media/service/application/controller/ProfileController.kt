package social.media.service.application.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import social.media.service.application.dto.request.ProfileRequest
import social.media.service.application.dto.request.ProfileUpdateRequest
import social.media.service.application.dto.request.toDomain
import social.media.service.application.dto.response.ProfileResponse
import social.media.service.application.dto.response.toResponse
import social.media.service.domain.services.ProfileService
import java.time.LocalDateTime
import javax.validation.Valid

@RestController
@RequestMapping("/profile")
class ProfileController(private val profileService: ProfileService) {

    @PostMapping("/{profileId}")
    fun register(
        @RequestBody @Valid request: ProfileRequest,
        @PathVariable profileId: String
    ): Mono<ResponseEntity<Any>> {
        val body: MutableMap<String, Any> = LinkedHashMap()

        return profileService.create(request.toDomain(profileId))
            .flatMap<ResponseEntity<Any>> {
                Mono.just(
                    ResponseEntity.status(HttpStatus.CREATED).body(it.toResponse())
                )
            }
            .onErrorResume { e ->

                body["timestamp"] = LocalDateTime.now()
                body["message"] = e.message!!

                Mono.just(
                    ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(body)
                )
            }
    }

    @PutMapping("/{profileId}")
    fun update(
        @RequestBody request: ProfileUpdateRequest,
        @PathVariable profileId: String
    ): Mono<ResponseEntity<ProfileResponse>> {
        return profileService.update(request.toDomain(profileId))
            .map { ResponseEntity.status(HttpStatus.CREATED).body(it.toResponse()) }
    }

    @PostMapping("/{profileId}/follow/{profileIdTo}")
    fun follow(@PathVariable profileId: String, @PathVariable profileIdTo: String): Mono<ResponseEntity<Void>> {
        return profileService.follow(profileId, profileIdTo)
            .map { ResponseEntity.ok().build() }
    }

    @GetMapping("/{profileId}")
    fun get(
        @PathVariable profileId: String
    ): Mono<ResponseEntity<ProfileResponse>> {
        return profileService.get(profileId)
            .map { ResponseEntity.status(HttpStatus.OK).body(it.toResponse()) }
    }
}