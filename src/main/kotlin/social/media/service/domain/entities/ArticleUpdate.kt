package social.media.service.domain.entities

data class ArticleUpdate(
    var title: String?,
    var body: String?,
    var tags: MutableList<Tag> = mutableListOf()
)