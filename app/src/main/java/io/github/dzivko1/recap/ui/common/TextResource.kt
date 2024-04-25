package io.github.dzivko1.recap.ui.common

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

interface TextResource {
  fun asString(context: Context): String

  companion object {
    fun fromString(string: String): TextResource = StringTextResource(string)

    fun fromStringRes(
      @StringRes resId: Int,
      vararg args: Any,
    ): TextResource = StringResTextResource(resId, args)
  }
}

@Composable
fun TextResource.asString() = asString(LocalContext.current)

private class StringTextResource(
  val string: String,
) : TextResource {
  override fun asString(context: Context): String = string
}

private class StringResTextResource(
  @StringRes val resId: Int,
  vararg val args: Any,
) : TextResource {
  override fun asString(context: Context): String {
    return context.getString(resId, args)
  }
}