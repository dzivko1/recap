package io.github.dzivko1.recap.ui.day.composable

import android.os.Build
import android.view.HapticFeedbackConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.dzivko1.recap.R
import io.github.dzivko1.recap.model.Record
import io.github.dzivko1.recap.ui.theme.Colors
import sh.calvin.reorderable.ReorderableCollectionItemScope

@Composable
fun ReorderableCollectionItemScope.RecordItem(
  record: Record,
  onClick: () -> Unit,
  onDeleteSwipe: () -> Unit,
) {
  val view = LocalView.current
  val threshold = 0.6f
  var dismissState: SwipeToDismissBoxState? = null
  dismissState = rememberSwipeToDismissBoxState(
    confirmValueChange = {
      when (it) {
        SwipeToDismissBoxValue.StartToEnd,
        SwipeToDismissBoxValue.EndToStart,
        -> {
          if (dismissState!!.progress > threshold) {
            view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
            onDeleteSwipe()
            true
          } else {
            false
          }
        }

        SwipeToDismissBoxValue.Settled -> false
      }
    },
    positionalThreshold = { it * threshold }
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