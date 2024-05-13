package io.github.dzivko1.recap.ui.day

import android.os.Build
import android.view.HapticFeedbackConstants
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.dzivko1.recap.R
import io.github.dzivko1.recap.model.Record
import io.github.dzivko1.recap.ui.theme.Colors
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.ReorderableItemScope
import sh.calvin.reorderable.rememberReorderableLazyColumnState
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DayScreen(
  date: LocalDate,
  records: List<Record>,
  startRecordOnOpen: Boolean,
  onSaveRecord: (id: String?, text: String) -> Unit,
  onDeleteRecord: (id: String) -> Unit,
  onMoveRecord: (from: Int, to: Int) -> Unit,
) {
  val listState = rememberLazyListState()
  val reorderableListState = rememberReorderableLazyColumnState(
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

      items(records, key = { it.id }) { record ->
        ReorderableItem(reorderableListState, key = record.id) {
          AnimatedContent(
            label = "Record edit transition",
            targetState = record.id == editedRecordId,
            transitionSpec = { fadeIn() togetherWith fadeOut() }
          ) { isEditing ->
            if (isEditing) {
              RecordInput(
                initialText = records.find { it.id == editedRecordId }?.text.orEmpty(),
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReorderableItemScope.RecordItem(
  record: Record,
  onClick: () -> Unit,
  onDeleteSwipe: () -> Unit,
) {
  val view = LocalView.current
  val dismissState = rememberSwipeToDismissBoxState(
    confirmValueChange = {
      when (it) {
        SwipeToDismissBoxValue.StartToEnd,
        SwipeToDismissBoxValue.EndToStart,
        -> {
          onDeleteSwipe()
          true
        }

        SwipeToDismissBoxValue.Settled -> false
      }
    }
  )

  SwipeToDismissBox(
    state = dismissState,
    backgroundContent = {
      Box(
        Modifier
          .fillMaxSize()
          .background(Colors.NegativeRed)
          .padding(12.dp),
        contentAlignment = if (dismissState.dismissDirection == SwipeToDismissBoxValue.StartToEnd) {
          Alignment.CenterStart
        } else {
          Alignment.CenterEnd
        }
      ) {
        Text(
          text = stringResource(R.string.day_delete_record_swipe_label)
        )
      }
    },
  ) {
    Surface(onClick = onClick) {
      Row(
        Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "‣ ${record.text}",
          style = MaterialTheme.typography.bodyMedium,
          modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
            .minimumInteractiveComponentSize()
            .weight(1f)
        )
        Icon(
          Icons.Default.DragHandle,
          contentDescription = null,
          modifier = Modifier.draggableHandle(
            onDragStarted = {
              if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                view.performHapticFeedback(HapticFeedbackConstants.DRAG_START)
              } else {
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
              }
            }
          )
        )
      }
    }
  }
}

@Composable
private fun RecordInput(
  initialText: String,
  onSaveClick: (text: String) -> Unit,
  onCancelClick: () -> Unit,
) {
  var text by rememberSaveable { mutableStateOf(initialText) }
  val focusRequester = remember { FocusRequester() }

  LaunchedEffect(Unit) {
    focusRequester.requestFocus()
  }

  BackHandler {
    onCancelClick()
  }

  TextField(
    value = text,
    onValueChange = { text = it },
    trailingIcon = {
      val enabled = text.isNotBlank()

      Row {
        IconButton(
          onClick = { onSaveClick(text) },
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
    modifier = Modifier
      .fillMaxWidth()
      .focusRequester(focusRequester)
  )
}