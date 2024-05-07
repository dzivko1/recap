package io.github.dzivko1.recap.ui.records

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.dzivko1.recap.R
import io.github.dzivko1.recap.model.Record
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordsScreen(
  records: List<Record>,
  onDaySelect: (LocalDate) -> Unit,
) {
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
          onClick = { onDaySelect(LocalDate.ofEpochDay(datePickerState.selectedDateMillis!! / 86400000)) }
        ) {
          Text(stringResource(R.string.records_date_picker_select_button).uppercase())
        }
      }
    ) {
      DatePicker(datePickerState)
    }
  }

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
      verticalArrangement = Arrangement.spacedBy(16.dp),
      contentPadding = PaddingValues(vertical = 16.dp)
    ) {
      items(records.groupBy { it.date }.toSortedMap().toList().asReversed()) { (date, records) ->
        DayItem(
          date = date,
          records = records,
          onClick = { onDaySelect(date) }
        )
      }
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