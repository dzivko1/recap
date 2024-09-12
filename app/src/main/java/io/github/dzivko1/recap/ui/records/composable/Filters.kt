package io.github.dzivko1.recap.ui.records.composable

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.dzivko1.recap.R
import io.github.dzivko1.recap.ui.records.TagFilter
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun TagFilterSection(
  filters: List<TagFilter>,
  onFilterToggle: (TagFilter) -> Unit,
  onFiltersConfirm: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val debounceMillis = 1000L
  var confirmJob by remember { mutableStateOf<Job?>(null) }
  val coroutineScope = rememberCoroutineScope()

  Row(
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    verticalAlignment = Alignment.CenterVertically,
    modifier = modifier.horizontalScroll(rememberScrollState())
  ) {
    TagFiltersDropdown(
      filters = filters,
      onFilterClick = onFilterToggle,
      onFiltersConfirm = onFiltersConfirm
    )
    filters.filter { it.isSelected }.forEach { filter ->
      FilterChip(
        selected = true,
        onClick = {
          onFilterToggle(filter)

          confirmJob?.cancel()
          confirmJob = coroutineScope.launch {
            delay(debounceMillis)
            onFiltersConfirm()
          }
        },
        label = { Text(filter.name) }
      )
    }
  }
}

@Composable
private fun TagFiltersDropdown(
  filters: List<TagFilter>,
  onFilterClick: (TagFilter) -> Unit,
  onFiltersConfirm: () -> Unit,
) {
  var expanded by remember { mutableStateOf(false) }

  Box {
    OutlinedButton(
      onClick = { expanded = true },
      shape = MaterialTheme.shapes.small,
      contentPadding = PaddingValues(8.dp)
    ) {
      Text(stringResource(R.string.records_filter_dropdown_label))
      Icon(Icons.Default.ArrowDropDown, contentDescription = null)
    }

    DropdownMenu(
      expanded = expanded,
      onDismissRequest = {
        expanded = false
        onFiltersConfirm()
      },
    ) {
      filters
        .filterNot { it.isSelected }
        .forEach { filter ->
          DropdownMenuItem(
            text = { Text(filter.name) },
            onClick = { onFilterClick(filter) }
          )
        }
    }
  }
}