package social.media.service.domain.services

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import social.media.service.domain.entities.Notification
import social.media.service.domain.entities.NotificationType
import social.media.service.domain.entities.Profile
import social.media.service.domain.entities.ProfileUpdate
import social.media.service.domain.exceptions.FieldAlreadyExistsException
import social.media.service.domain.repositories.ProfileRepository
import java.time.LocalDateTime

@Service
class ProfileService(
    private val profileRepository: ProfileRepository,
    private val notificationService: NotificationService
) {

    private val log: Logger = LoggerFactory.getLogger(ProfileService::class.java)

    fun create(profile: Profile): Mono<Profile> {
        return profileRepository.existsByEmail(profile.email)
            .map {
                if (it != false)
                    throw FieldAlreadyExistsException("Email or username already exist")
            }
            .flatMap {
                profileRepository.existsByUsername(profile.username)
                    .map {
                        if (it != false)
                            throw FieldAlreadyExistsException("Email or username already exist")
                    }
            }
            .flatMap {
                profileRepository.save(profile)
                    .doOnNext {
                        log.info("Profile persisted successfully - {}", profile)
                    }
            }
    }

    fun get(profileId: String): Mono<Profile> = profileRepository.findById(profileId)

    fun update(profile: ProfileUpdate): Mono<Profile> {
        return profileRepository.findById(profile.profileId)
            .doOnNext { log.info("Find profile by id - {}", profile.profileId) }
            .flatMap { document ->

                document.about = profile.about ?: document.about
                document.image = profile.image ?: document.image
                document.updatedAt = LocalDateTime.now()

                profileRepository.save(document)
            }
            .doOnNext { log.info("Updated profile persisted successfully - {}", profile) }
    }

    fun follow(profileId: String, profileIdTo: String): Mono<Void> {
        var following = false
        var follow = false

        return profileRepository.findById(profileId)
            .flatMap {

                if (!it.following.contains(profileIdTo)) {
                    it.following.add(profileIdTo).also {
                        following = true
                    }

                    log.info("Follow persisted in profile {} with successfully", profileId)
                } else {
                    it.following.remove(profileIdTo)

                    log.info("Follow deleted in profile {} with successfully", profileId)
                }

                profileRepository.save(it)
            }
            .flatMap { profileRepository.findById(profileIdTo) }
            .flatMap {
                if (!it.followers.contains(profileId)) {
                    it.followers.add(profileId).also {
                        follow = true
                    }

                    log.info("Follow persisted in profile {} with successfully", profileIdTo)
                } else {
                    it.followers.remove(profileId)

                    log.info("Follow deleted in profile {} with successfully", profileIdTo)
                }

                profileRepository.save(it)
            }
            .filter { following && follow }
            .flatMap {
                val notification = Notification(
                    fromProfileId = profileId,
                    toProfileId = profileIdTo,
                    notificationType = NotificationType.FOLLOW
                )
                notificationService.sendMessage(notification)
            }
    }

}