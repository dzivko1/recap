package io.github.dzivko1.recap.ui.day.composable

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.dzivko1.recap.R
import io.github.dzivko1.recap.ui.day.DayUiState
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DayScreen(
  date: LocalDate,
  uiState: DayUiState,
  startRecordOnOpen: Boolean,
  onSaveRecord: (id: String?, text: String) -> Unit,
  onDeleteRecord: (id: String) -> Unit,
  onMoveRecord: (from: Int, to: Int) -> Unit,
) {
  val listState = rememberLazyListState()
  val reorderableListState = rememberReorderableLazyListState(
    lazyListState = listState,
    onMove = { from, to ->
      // Adjusting index to account for list items that aren't reorderable
      onMoveRecord(from.index - 1, to.index - 1)
    }
  )

  var editedRecordId by rememberSaveable { mutableStateOf(if (startRecordOnOpen) "" else null) }

  Scaffold { contentPadding ->
    LazyColumn(
      Modifier
        .fillMaxSize()
        .padding(contentPadding),
      state = listState,
      contentPadding = PaddingValues(vertical = 16.dp)
    ) {
      item {
        Text(
          text = date.format(
            DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)
              // TODO: have in Settings
              .withLocale(Locale.forLanguageTag("hr"))
          ),
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          modifier = Modifier.padding(horizontal = 12.dp)
        )
      }

      items(uiState.records, key = { it.id }) { record ->
        ReorderableItem(reorderableListState, key = record.id) {
          AnimatedContent(
            label = "Record edit transition",
            targetState = record.id == editedRecordId,
            transitionSpec = { fadeIn() togetherWith fadeOut() }
          ) { isEditing ->
            if (isEditing) {
              RecordInput(
                initialText = uiState.records.find { it.id == editedRecordId }?.text.orEmpty(),
                availableTags = uiState.availableTags,
                onSaveClick = { text ->
                  onSaveRecord(editedRecordId, text)
                  editedRecordId = null
                },
                onCancelClick = { editedRecordId = null }
              )
            } else {
              RecordItem(
                record = record,
                onClick = { editedRecordId = record.id },
                onDeleteSwipe = { onDeleteRecord(record.id) }
              )
            }
          }
        }
      }

      item {
        AnimatedContent(
          label = "New record transition",
          targetState = editedRecordId?.isEmpty() == true
        ) { isCreatingRecord ->
          if (isCreatingRecord) {
            RecordInput(
              initialText = "",
              availableTags = uiState.availableTags,
              onSaveClick = { text ->
                onSaveRecord(null, text)
                editedRecordId = null
              },
              onCancelClick = { editedRecordId = null }
            )
          } else {
            Box(
              contentAlignment = Alignment.Center,
              modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp)
            ) {
              Button(
                onClick = { editedRecordId = "" },
                modifier = Modifier.fillMaxWidth(0.3f)
              ) {
                Icon(
                  Icons.Default.Add,
                  contentDescription = stringResource(R.string.day_new_record_content_desc)
                )
              }
            }
          }
        }
      }
    }
  }
}