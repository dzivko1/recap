package io.github.dzivko1.recap.model

data class TagInfo(
  val name: String,
  val count: Int,
)

data class TagInfoApiModel(
  val name: String = "",
  val count: Int = 0,
)

fun TagInfo.toApiModel(): TagInfoApiModel {
  return TagInfoApiModel(
    name = name,
    count = count
  )
}

fun TagInfoApiModel.toDomainModel(): TagInfo {
  return TagInfo(
    name = name,
    count = count
  )
}