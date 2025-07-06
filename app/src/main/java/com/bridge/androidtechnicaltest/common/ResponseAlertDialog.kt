package com.bridge.androidtechnicaltest.common


import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun ResponseAlertDialog(
    title: String,
    message: String? = null,
    confirmButtonText: String,
    onConfirm: () -> Unit,
    onDismiss: (() -> Unit)? = null,
    confirmButtonColor: Color = MaterialTheme.colorScheme.primary,
    custom: (@Composable () -> Unit)? = null
) {
    AlertDialog(
        onDismissRequest = { },
        title = { Text(title) },
        text = {
            custom?.invoke() ?: message?.let { Text(it) }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = confirmButtonColor
                )
            ) {
                Text(confirmButtonText, color = Color.White)
            }
        },
        dismissButton = {
            if (onDismiss != null) {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        }
    )
}
