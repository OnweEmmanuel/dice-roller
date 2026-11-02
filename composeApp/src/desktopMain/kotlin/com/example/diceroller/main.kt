package com.example.diceroller

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Dice Roller",
        state = rememberWindowState(width = 400.dp, height = 500.dp),
    ) {
        App()
    }
}
