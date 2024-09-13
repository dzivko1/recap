package io.github.dzivko1.recap.ui.common.component.input

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation

// TODO According to https://issuetracker.google.com/issues/235383908, the support to assign
//  onTextLayout to TextField will come in an upcoming release. Check and remove this when it's available.

/**
 * Rough mimic of [androidx.compose.material3.TextField] which additionally exposes the [onTextLayout] callback.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordTextField(
  value: TextFieldValue,
  onValueChange: (TextFieldValue) -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  readOnly: Boolean = false,
  textStyle: TextStyle = LocalTextStyle.current,
  label: @Composable (() -> Unit)? = null,
  placeholder: @Composable (() -> Unit)? = null,
  leadingIcon: @Composable (() -> Unit)? = null,
  trailingIcon: @Composable (() -> Unit)? = null,
  prefix: @Composable (() -> Unit)? = null,
  suffix: @Composable (() -> Unit)? = null,
  supportingText: @Composable (() -> Unit)? = null,
  isError: Boolean = false,
  visualTransformation: VisualTransformation = VisualTransformation.None,
  keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
  keyboardActions: KeyboardActions = KeyboardActions.Default,
  singleLine: Boolean = false,
  maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
  minLines: Int = 1,
  interactionSource: MutableInteractionSource? = null,
  shape: Shape = TextFieldDefaults.shape,
  colors: TextFieldColors = TextFieldDefaults.colors(),
  onTextLayout: (TextLayoutResult) -> Unit = {},
) {
  @Suppress("NAME_SHADOWING")
  val interactionSource = interactionSource ?: remember { MutableInteractionSource() }
  // If color is not provided via the text style, use content color as a default
  val textColor =
    textStyle.color.takeOrElse {
      val focused = interactionSource.collectIsFocusedAsState().value
      with(colors) {
        when {
          !enabled -> disabledTextColor
          isError -> errorTextColor
          focused -> focusedTextColor
          else -> unfocusedTextColor
        }
      }
    }
  val mergedTextStyle = textStyle.merge(TextStyle(color = textColor))

  CompositionLocalProvider(LocalTextSelectionColors provides colors.textSelectionColors) {
    BasicTextField(
      value = value,
      modifier =
      modifier.defaultMinSize(
        minWidth = TextFieldDefaults.MinWidth,
        minHeight = TextFieldDefaults.MinHeight
      ),
      onValueChange = onValueChange,
      enabled = enabled,
      readOnly = readOnly,
      textStyle = mergedTextStyle,
      cursorBrush = SolidColor(colors.cursorColor),
      visualTransformation = visualTransformation,
      keyboardOptions = keyboardOptions,
      keyboardActions = keyboardActions,
      interactionSource = interactionSource,
      singleLine = singleLine,
      maxLines = maxLines,
      minLines = minLines,
      onTextLayout = onTextLayout,
      decorationBox =
      @Composable { innerTextField ->
        // places leading icon, text field with label and placeholder, trailing icon
        TextFieldDefaults.DecorationBox(
          value = value.text,
          visualTransformation = visualTransformation,
          innerTextField = innerTextField,
          placeholder = placeholder,
          label = label,
          leadingIcon = leadingIcon,
          trailingIcon = trailingIcon,
          prefix = prefix,
          suffix = suffix,
          supportingText = supportingText,
          shape = shape,
          singleLine = singleLine,
          enabled = enabled,
          isError = isError,
          interactionSource = interactionSource,
          colors = colors
        )
      }
    )
  }
}