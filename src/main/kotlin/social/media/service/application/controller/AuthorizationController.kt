package social.media.service.application.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import social.media.service.domain.entities.Login
import social.media.service.domain.services.AuthorizationService
import java.time.LocalDateTime
import javax.validation.Valid

@RestController
@RequestMapping("/auth")
class AuthorizationController(private val authorizationService: AuthorizationService) {

    @PostMapping("/login")
    fun login(
        @RequestBody @Valid request: Login,
    ): Mono<ResponseEntity<Any>> {
        return authorizationService.authorization(request)
            .flatMap<ResponseEntity<Any>> {
                Mono.just(
                    ResponseEntity.status(HttpStatus.CREATED)
                        .header("Authorization", "Bearer $it")
                        .build()
                )
            }
            .onErrorResume { e ->
                val body: MutableMap<String, Any> = LinkedHashMap()
                body["timestamp"] = LocalDateTime.now()
                body["message"] = e.message!!

                Mono.just(
                    ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(body)
                )
            }
    }

}