package io.github.dzivko1.recap.ui.records

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.dzivko1.recap.model.Record

@Composable
fun RecordsScreen(
  records: List<Record>,
) {
  Scaffold { contentPadding ->
    LazyColumn(Modifier.padding(contentPadding)) {
      items(records) { record ->
        Text(text = record.text)
      }
    }
  }
}