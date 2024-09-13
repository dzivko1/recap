package io.github.dzivko1.recap.ui.day.composable

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import io.github.dzivko1.recap.R
import io.github.dzivko1.recap.ui.common.component.input.RecordTextField
import io.github.dzivko1.recap.ui.common.toDpOffset
import io.github.dzivko1.recap.ui.theme.Colors

@Composable
fun RecordInput(
  initialText: String,
  availableTags: List<String>,
  onSaveClick: (text: String) -> Unit,
  onCancelClick: () -> Unit,
) {
  val density = LocalDensity.current
  val focusRequester = remember { FocusRequester() }

  var text by rememberSaveable(stateSaver = TextFieldValue.Saver) {
    mutableStateOf(
      TextFieldValue(initialText, selection = TextRange(initialText.length))
    )
  }
  var tagSuggestions by remember { mutableStateOf<List<String>>(emptyList()) }
  var tagSuggestionsPosition by remember { mutableStateOf(DpOffset.Zero) }

  LaunchedEffect(Unit) {
    focusRequester.requestFocus()
  }

  BackHandler {
    onCancelClick()
  }

  Box {
    RecordTextField(
      value = text,
      onValueChange = { value ->
        text = value
        tagSuggestions = detectPossibleTags(value, availableTags)
      },
      trailingIcon = {
        val enabled = text.text.isNotBlank()

        Row {
          IconButton(
            onClick = { onSaveClick(text.text) },
            enabled = enabled
          ) {
            Icon(
              Icons.Default.Check,
              contentDescription = stringResource(R.string.day_save_record_content_desc),
              tint = if (enabled) Colors.PositiveGreen else Colors.PositiveGreen.copy(alpha = LocalContentColor.current.alpha)
            )
          }
          IconButton(onClick = onCancelClick) {
            Icon(
              Icons.Default.Close,
              contentDescription = stringResource(R.string.day_cancel_record_edit_content_desc),
              tint = Colors.NegativeRed
            )
          }
        }
      },
      onTextLayout = { textLayoutResult ->
        if (tagSuggestions.isEmpty()) {
          tagSuggestionsPosition = textLayoutResult.getCursorRect(text.selection.end).bottomRight
            .toDpOffset(density) + DpOffset(0.dp, -24.dp)
        }
      },
      modifier = Modifier
        .fillMaxWidth()
        .focusRequester(focusRequester)
    )

    TagSuggestions(
      tags = tagSuggestions,
      onTagClick = { tag ->
        text = completeTag(text, tag)
        tagSuggestions = emptyList()
      },
      position = tagSuggestionsPosition,
      expanded = tagSuggestions.isNotEmpty(),
      onDismissRequest = { tagSuggestions = emptyList() },
    )
  }
}

/**
 * Finds possible tag candidates from [availableTags] based on the given text and cursor position.
 */
private fun detectPossibleTags(
  textFieldValue: TextFieldValue,
  availableTags: List<String>,
): List<String> {
  if (!textFieldValue.selection.collapsed) return emptyList()

  // Substring between the cursor and the first tag mark to its left
  val potentialTag = textFieldValue.text.substring(0, textFieldValue.selection.end)
    .substringAfterLast("#", missingDelimiterValue = " ")
    .also { if (it == " ") return emptyList() }

  // If there is no whitespace between the tag mark and the cursor, consider it a potential tag and
  // find all existing tags that start with it
  return if (potentialTag.matches(Regex("\\S*"))) {
    availableTags.filter { tag ->
      tag.startsWith(potentialTag, ignoreCase = true)
    }
  } else {
    emptyList()
  }
}

/**
 * Returns a new [TextFieldValue] based on the given one, where the given [tag] is inserted/completed
 * at the cursor position
 */
private fun completeTag(textFieldValue: TextFieldValue, tag: String): TextFieldValue {
  val beforeTag = textFieldValue.text.substring(0, textFieldValue.selection.end)
    .substringBeforeLast("#", missingDelimiterValue = "")
  val afterTag = textFieldValue.text.substring(textFieldValue.selection.end)

  return textFieldValue.copy(
    text = "$beforeTag#$tag$afterTag",
    selection = TextRange(beforeTag.length + tag.length + 1)
  )
}