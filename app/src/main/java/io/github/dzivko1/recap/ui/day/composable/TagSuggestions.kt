package io.github.dzivko1.recap.ui.day.composable

import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties

@Composable
fun TagSuggestions(
  tags: List<String>,
  onTagClick: (String) -> Unit,
  onDismissRequest: () -> Unit,
  position: DpOffset,
  expanded: Boolean,
  modifier: Modifier = Modifier,
) {
  DropdownMenu(
    expanded = expanded,
    onDismissRequest = onDismissRequest,
    offset = position,
    properties = PopupProperties(focusable = false),
    modifier = modifier.heightIn(max = 280.dp)
  ) {
    tags.forEach { tag ->
      DropdownMenuItem(
        text = { Text(tag) },
        onClick = { onTagClick(tag) }
      )
    }
  }
}