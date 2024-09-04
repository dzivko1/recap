package io.github.dzivko1.recap.ui.records

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.dzivko1.recap.R
import io.github.dzivko1.recap.data.record.RecordRepository
import io.github.dzivko1.recap.model.Record
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordsScreen(
  uiState: RecordsUiState,
  onDaySelect: (LocalDate, startRecord: Boolean) -> Unit,
  onRequestMoreRecords: () -> Unit,
) {
  val listState = rememberLazyListState()
  var dayInputVisible by remember { mutableStateOf(false) }
  val datePickerState = rememberDatePickerState(
    initialSelectedDateMillis = System.currentTimeMillis()
  )

  if (dayInputVisible) {
    DatePickerDialog(
      onDismissRequest = { dayInputVisible = false },
      confirmButton = {
        TextButton(
          enabled = datePickerState.selectedDateMillis != null,
          onClick = {
            onDaySelect(
              LocalDate.ofEpochDay(datePickerState.selectedDateMillis!! / 86400000),
              true
            )
          }
        ) {
          Text(stringResource(R.string.records_date_picker_select_button).uppercase())
        }
      }
    ) {
      DatePicker(datePickerState)
    }
  }

  LazyLoadEffect(
    listState = listState,
    onRequestMoreRecords = onRequestMoreRecords
  )

  Scaffold(
    floatingActionButton = {
      FloatingActionButton(onClick = { dayInputVisible = true }) {
        Icon(
          Icons.Default.Create,
          contentDescription = stringResource(R.string.records_add_record_content_desc)
        )
      }
    }
  ) { contentPadding ->
    LazyColumn(
      Modifier.padding(contentPadding),
      state = listState,
      verticalArrangement = Arrangement.spacedBy(16.dp),
      contentPadding = PaddingValues(vertical = 16.dp)
    ) {
      when {
        uiState.records?.isEmpty() == true -> {
          item {
            Box(
              Modifier
                .fillMaxSize()
                .padding(vertical = 32.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(stringResource(R.string.records_empty_message))
            }
          }
        }

        uiState.records != null -> {
          items(
            uiState.records.groupBy { it.date }.toSortedMap().toList().asReversed(),
            key = { (date, _) -> date }
          ) { (date, records) ->
            DayItem(
              date = date,
              records = records,
              onClick = { onDaySelect(date, false) }
            )
          }
        }
      }

      when {
        uiState.recordLoadingError != null -> {
          item(key = KEY_ERROR_ITEM) {
            Box(
              Modifier
                .fillMaxSize()
                .padding(vertical = 16.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                stringResource(
                  R.string.page_loading_error,
                  uiState.recordLoadingError.localizedMessage
                    ?: stringResource(R.string.general_unknown)
                )
              )
            }
          }
        }

        uiState.areRecordsLoading -> {
          item {
            Box(
              Modifier
                .fillMaxSize()
                .padding(vertical = 16.dp),
              contentAlignment = Alignment.Center
            ) {
              CircularProgressIndicator()
            }
          }
        }
      }
    }
  }
}

@Composable
private fun LazyLoadEffect(
  listState: LazyListState,
  onRequestMoreRecords: () -> Unit,
) {
  var isLoadingMore by remember { mutableStateOf(false) }
  val firstVisibleItemIndex by remember { derivedStateOf { listState.firstVisibleItemIndex } }
  val layoutInfo by remember { derivedStateOf { listState.layoutInfo } }

  LaunchedEffect(firstVisibleItemIndex) {
    val totalItemsCount = listState.layoutInfo.totalItemsCount

    if (!isLoadingMore && totalItemsCount > 0 &&
      firstVisibleItemIndex >= totalItemsCount - RecordRepository.RECORD_PAGE_SIZE / 2
    ) {
      isLoadingMore = true
      onRequestMoreRecords()
    }
  }

  LaunchedEffect(layoutInfo.totalItemsCount) {
    isLoadingMore = false
  }

  LaunchedEffect(layoutInfo.visibleItemsInfo) {
    if (listState.layoutInfo.visibleItemsInfo.lastOrNull()?.key == KEY_ERROR_ITEM) {
      isLoadingMore = false
    }
  }
}

@Composable
private fun DayItem(
  date: LocalDate,
  records: List<Record>,
  onClick: () -> Unit,
) {
  Surface(onClick = onClick) {
    Column(
      Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 8.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Text(
        text = date.format(
          DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)
            // TODO: have in Settings
            .withLocale(Locale.forLanguageTag("hr"))
        ),
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
      )

      records.forEach { record ->
        Text(
          text = "‣ ${record.text}",
          style = MaterialTheme.typography.bodyMedium,
          modifier = Modifier.padding(horizontal = 4.dp)
        )
      }
    }
  }
}

private const val KEY_ERROR_ITEM = "error"