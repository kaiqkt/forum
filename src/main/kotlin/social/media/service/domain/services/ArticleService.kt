package social.media.service.domain.services

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import social.media.service.domain.entities.*
import social.media.service.domain.repositories.ArticleRepository
import social.media.service.domain.repositories.ProfileRepository
import social.media.service.domain.repositories.TagRepository
import java.time.LocalDateTime


@Service
class ArticleService(
    private val articleRepository: ArticleRepository,
    private val tagRepository: TagRepository,
    private val profileRepository: ProfileRepository,
    private val notificationService: NotificationService
) {
    private val log: Logger = LoggerFactory.getLogger(ArticleService::class.java)

    fun create(article: Article): Mono<Article> {

        return validTags(article.tags)
            .flatMap {
                article.tags = it
                articleRepository.save(article)
            }
            .doOnNext { log.info("Article persisted successfully - {}", article) }
            .flatMap { profileRepository.findById(article.profileId) }
            .doOnNext { log.info("Profile find by id - {}", article.profileId) }
            .filter { it.followers.isNotEmpty() }
            .flatMap {
                return@flatMap Flux.fromIterable(it.followers)
                    .flatMap { follower ->
                        val notification = Notification(
                            fromProfileId = article.profileId,
                            toProfileId = follower,
                            notificationType = NotificationType.NEW_ARTICLE
                        )

                        notificationService.sendMessage(notification)
                    }
                    .next()
            }
            .flatMap { Mono.just(article) }
    }

    fun update(article: ArticleUpdate, articleId: String): Mono<Article> {
        var tags: MutableList<Tag> = mutableListOf()

        return validTags(article.tags)
            .map { validTags -> tags = validTags }
            .flatMap { articleRepository.findById(articleId) }
            .doOnNext { log.info("Article find by id - {}", articleId) }
            .flatMap { document ->

                document.title = article.title ?: document.title
                document.body = article.body ?: document.body
                document.tags = if (tags.isEmpty()) document.tags else tags
                document.updatedAt = LocalDateTime.now()

                log.info("Article persisted successfully - {}", article)

                articleRepository.save(document)
            }
    }

    fun delete(articleId: String): Mono<Void> {
        return articleRepository.deleteById(articleId)
            .doOnNext { log.info("Article deleted successfully - {}", articleId) }
    }

    fun getArticle(articleId: String): Mono<Article> {
        return articleRepository.findById(articleId)
            .doOnNext { log.info("Article find by id - {}", articleId) }
    }

    fun getFeed(profileId: String): Flux<Article> {
        return profileRepository.findById(profileId)
            .map { profile -> profile.following }
            .flatMapMany {
                log.info("Find feed by id - {}", profileId)

                articleRepository.findByProfileIdInOrderByCreatedAtAsc(it)
            }
    }

    fun addComment(articleId: String, comment: Comment): Mono<Article> {
        return articleRepository.findById(articleId)
            .doOnNext { log.info("Article find by id - {}", articleId) }
            .flatMap { document ->
                document.comments.add(comment)

                articleRepository.save(document)
                    .doOnNext { log.info("Comment in article {} persisted successfully", articleId) }
                    .flatMap {
                        val notification = Notification(
                            fromProfileId = comment.profileId,
                            toProfileId = it.profileId,
                            notificationType = NotificationType.COMMENT
                        )
                        notificationService.sendMessage(notification)
                    }
                    .flatMap { Mono.just(document) }
            }
    }

    fun deleteComment(articleId: String, commentId: String): Mono<Article> {
        return articleRepository.findById(articleId)
            .flatMap { document ->
                document.comments.map { comment ->
                    if (comment.id == commentId)
                        document.comments.remove(comment)
                }

                log.info("Comment in article {} deleted successfully", articleId)

                articleRepository.save(document)
            }
    }

    fun like(profileId: String, articleId: String): Mono<Void> {
        var like = false

        return articleRepository.findById(articleId)
            .doOnNext { log.info("Article find by id - {}", articleId) }
            .flatMap { document ->

                if (!document.likes.contains(profileId)) {
                    document.likes.add(profileId).also {
                        like = true
                    }

                    log.info("Like persisted in article {} with successfully", articleId)
                } else {
                    document.likes.remove(profileId)

                    log.info("Like deleted in article {} with successfully", articleId)
                }

                articleRepository.save(document)
            }
            .filter { like }
            .flatMap {
                val notification = Notification(
                    fromProfileId = profileId,
                    toProfileId = it.profileId,
                    notificationType = NotificationType.LIKE
                )

                notificationService.sendMessage(notification)
            }
    }


    private fun validTags(tags: MutableList<Tag>): Mono<MutableList<Tag>> {
        return Flux.fromIterable(tags)
            .flatMap { tag -> tagRepository.findById(tag.id) }
            .filter { it != null }
            .collectList()
    }
}




