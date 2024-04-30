package io.github.dzivko1.recap.ui.common.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.zIndex

@Composable
fun PageLoadingIndicator() {
  Box(
    Modifier
      .clickable {} // no-op (consume clicks)
      .fillMaxSize()
      .zIndex(2f)
      .background(Color.Black.copy(alpha = 0.5f)),
    contentAlignment = Alignment.Center
  ) {
    CircularProgressIndicator(color = Color.Gray)
  }
}