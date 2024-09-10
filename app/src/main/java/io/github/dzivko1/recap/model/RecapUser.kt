package io.github.dzivko1.recap.model

import com.google.firebase.firestore.DocumentId

data class RecapUser(
  val id: String,
  val tagSummary: TagSummary,
)

data class RecapUserApiModel(
  @DocumentId
  val id: String = "",
  val tags: List<TagInfoApiModel> = emptyList(),
)

fun RecapUser.toApiModel(): RecapUserApiModel {
  return RecapUserApiModel(
    id = id,
    tags = tagSummary.tags.map { it.toApiModel() }
  )
}

fun RecapUserApiModel.toDomainModel(): RecapUser {
  return RecapUser(
    id = id,
    tagSummary = TagSummary(tags.map { it.toDomainModel() })
  )
}