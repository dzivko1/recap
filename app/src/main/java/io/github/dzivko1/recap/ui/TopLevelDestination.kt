package io.github.dzivko1.recap.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.History
import androidx.compose.ui.graphics.vector.ImageVector
import io.github.dzivko1.recap.R
import io.github.dzivko1.recap.ui.calendar.CALENDAR_SECTION_ROUTE
import io.github.dzivko1.recap.ui.common.TextResource
import io.github.dzivko1.recap.ui.records.RECORDS_SECTION_ROUTE
import io.github.dzivko1.recap.ui.reminders.REMINDERS_SECTION_ROUTE

enum class TopLevelDestination(
  val route: String,
  val label: TextResource,
  val icon: ImageVector,
) {
  Records(
    route = RECORDS_SECTION_ROUTE,
    label = TextResource.fromStringRes(R.string.records_section_label),
    icon = Icons.Default.History
  ),
  Calendar(
    route = CALENDAR_SECTION_ROUTE,
    label = TextResource.fromStringRes(R.string.calendar_section_label),
    icon = Icons.Default.CalendarMonth
  ),
  Reminders(
    route = REMINDERS_SECTION_ROUTE,
    label = TextResource.fromStringRes(R.string.reminders_section_label),
    icon = Icons.Default.Alarm
  )
}