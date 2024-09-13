package io.github.dzivko1.recap.ui.common

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpOffset

fun Offset.toDpOffset(density: Density): DpOffset {
  return with(density) { DpOffset(x.toDp(), y.toDp()) }
}