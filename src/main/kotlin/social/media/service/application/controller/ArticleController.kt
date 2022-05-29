package social.media.service.application.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import social.media.service.application.dto.request.ArticleRequest
import social.media.service.application.dto.request.ArticleUpdateRequest
import social.media.service.application.dto.request.CommentRequest
import social.media.service.application.dto.request.toDomain
import social.media.service.application.dto.response.ArticleResponse
import social.media.service.application.dto.response.toResponse
import social.media.service.domain.services.ArticleService
import javax.validation.Valid

@RestController
@RequestMapping("/article")
class ArticleController(private val articleService: ArticleService) {

    @PostMapping("/{profileId}")
    fun createArticle(
        @RequestBody @Valid request: ArticleRequest,
        @PathVariable profileId: String
    ): Mono<ResponseEntity<ArticleResponse>> {
        return articleService.create(request.toDomain(profileId))
            .map { ResponseEntity.status(HttpStatus.CREATED).body(it.toResponse()) }
    }

    @PutMapping("/{profileId}/update/{articleId}")
    fun updateArticle(
        @RequestBody request: ArticleUpdateRequest,
        @PathVariable profileId: String,
        @PathVariable articleId: String
    ): Mono<ResponseEntity<ArticleResponse>> {
        return articleService.update(request.toDomain(), articleId)
            .map { ResponseEntity.status(HttpStatus.CREATED).body(it.toResponse()) }
    }

    @DeleteMapping("/{articleId}")
    fun deleteArticle(
        @PathVariable articleId: String
    ): Mono<ResponseEntity<Void>> {
        return articleService.delete(articleId)
            .map(ResponseEntity.ok()::body)
    }

    @GetMapping("/{articleId}")
    fun getArticle(@PathVariable articleId: String): Mono<ResponseEntity<ArticleResponse>> {
        return articleService.getArticle(articleId)
            .map { ResponseEntity.status(HttpStatus.CREATED).body(it.toResponse()) }
    }


    @GetMapping("/{profileId}/feed")
    fun feed(@PathVariable profileId: String): Mono<ResponseEntity<Flux<ArticleResponse>>> {
        val response = articleService.getFeed(profileId)

        return Mono.just(ResponseEntity.ok().body(response.map { it.toResponse() }))
    }

    @PostMapping("/{profileId}/like/{articleId}")
    fun like(@PathVariable profileId: String, @PathVariable articleId: String): Mono<ResponseEntity<Void>> {
        return articleService.like(profileId, articleId)
            .map(ResponseEntity.ok()::body)
    }

    @PostMapping("/{profileId}/comment/{articleId}")
    fun addComment(
        @PathVariable profileId: String,
        @RequestBody @Valid request: CommentRequest,
        @PathVariable articleId: String,
    ): Mono<ResponseEntity<ArticleResponse>> {
        return articleService.addComment(articleId, request.toDomain(articleId, profileId))
            .map { ResponseEntity.status(HttpStatus.CREATED).body(it.toResponse()) }
    }

    @DeleteMapping("/{articleId}/comment/{commentId}")
    fun deleteComment(
        @PathVariable articleId: String,
        @PathVariable commentId: String
    ): Mono<ResponseEntity<ArticleResponse>> {
        return articleService.deleteComment(articleId, commentId)
            .map { ResponseEntity.status(HttpStatus.CREATED).body(it.toResponse()) }
    }
}