package com.example.diceroller

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.unit.Density
import androidx.compose.ui.use
import org.jetbrains.skia.EncodedImageFormat
import java.io.File

/**
 * Renders the app offscreen and writes the README screenshots.
 * Run with `./gradlew :composeApp:readmeScreenshots`.
 */
@OptIn(ExperimentalComposeUiApi::class)
fun main(args: Array<String>) {
    val outDir = File(args.firstOrNull() ?: "docs/images").apply { mkdirs() }
    val width = 360
    val height = 640
    val density = 2f
    val start = System.nanoTime()
    fun now() = System.nanoTime() - start

    ImageComposeScene(
        width = (width * density).toInt(),
        height = (height * density).toInt(),
        density = Density(density),
    ) { App() }.use { scene ->
        fun capture(name: String) {
            val image = scene.render(now())
            val bytes = image.encodeToData(EncodedImageFormat.PNG)!!.bytes
            File(outDir, "$name.png").writeBytes(bytes)
            println("Wrote ${File(outDir, "$name.png")}")
        }
        // Let resources load and the first layout settle.
        fun settle(ms: Long) {
            val until = System.currentTimeMillis() + ms
            while (System.currentTimeMillis() < until) {
                scene.render(now())
                Thread.sleep(16)
            }
        }

        settle(1_000)

        // The Roll button sits just below the vertically centred die.
        val button = Offset(width * density / 2, height * density / 2 + 135 * density)
        scene.sendPointerEvent(PointerEventType.Press, button)
        scene.sendPointerEvent(PointerEventType.Release, button)

        settle(300)
        capture("rolling")

        settle(2_000)
        capture("rolled")
    }
}
