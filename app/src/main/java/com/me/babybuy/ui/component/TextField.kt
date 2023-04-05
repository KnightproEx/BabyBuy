@file:OptIn(ExperimentalMaterial3Api::class)

package com.me.babybuy.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

/**
 * A [Composable] function for custom text field Composable.
 */
@Composable
fun AppTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean = false,
    errorMessage: String = "",
    keyboardOptions: KeyboardOptions,
    label: String,
    multiLine: Boolean = false,
    maxLines: Int = 3,
    trailingIcon: @Composable () -> Unit = {},
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = value,
            onValueChange = onValueChange,
            isError = isError,
            keyboardOptions = keyboardOptions,
            label = { Text(label) },
            singleLine = !multiLine,
            maxLines = maxLines,
            trailingIcon = trailingIcon
        )
        if (isError) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

/**
 * A [Composable] function for custom password text field Composable.
 */
@Composable
fun PasswordTextField(
    modifier: Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean = false,
    errorMessage: String = "",
    keyboardOptions: KeyboardOptions,
    label: String,
) {
    var show by remember { mutableStateOf(false) }
    Column(modifier = modifier) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = value,
            onValueChange = onValueChange,
            isError = isError,
            keyboardOptions = keyboardOptions,
            label = { Text(label) },
            trailingIcon = {
                IconButton(onClick = { show = !show }) {
                    Icon(if (!show) Icons.Filled.VisibilityOff else Icons.Filled.Visibility, "")
                }
            },
            visualTransformation = if (!show) PasswordVisualTransformation() else VisualTransformation.None,
            singleLine = true
        )
        if (isError) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}