package io.github.dzivko1.recap.model

class TagSummary(
  val tags: List<TagInfo>,
) {
  fun withNewTags(newTags: List<String>): TagSummary {
    val result = tags.toMutableList()
    newTags.forEach { tag ->
      val index = result.indexOfFirst { it.name == tag }
      if (index != -1) {
        result[index] = result[index].copy(count = result[index].count + 1)
      } else {
        result += TagInfo(name = tag, count = 1)
      }
    }
    return TagSummary(result)
  }

  fun withRemovedTags(oldTags: List<String>): TagSummary {
    val result = tags.toMutableList()
    oldTags.forEach { tag ->
      val index = result.indexOfFirst { it.name == tag }
      if (index != -1) {
        if (result[index].count > 1) {
          result[index] = result[index].copy(count = result[index].count - 1)
        } else {
          result.removeAt(index)
        }
      }
    }
    return TagSummary(result)
  }
}

