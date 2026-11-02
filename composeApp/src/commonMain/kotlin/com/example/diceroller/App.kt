package com.example.diceroller

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import diceroller.composeapp.generated.resources.Res
import diceroller.composeapp.generated.resources.dice_1
import diceroller.composeapp.generated.resources.dice_2
import diceroller.composeapp.generated.resources.dice_3
import diceroller.composeapp.generated.resources.dice_4
import diceroller.composeapp.generated.resources.dice_5
import diceroller.composeapp.generated.resources.dice_6
import diceroller.composeapp.generated.resources.empty_dice
import diceroller.composeapp.generated.resources.empty_dice_description
import diceroller.composeapp.generated.resources.rolled_description
import diceroller.composeapp.generated.resources.roll
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlin.random.Random

private const val ROLL_DURATION_MS = 900

@Composable
fun App() {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            // 0 means the die hasn't been rolled yet.
            var value by rememberSaveable { mutableStateOf(0) }
            // The face shown while tumbling; settles on [value] when the roll ends.
            var shownFace by remember { mutableStateOf(value) }
            var rolling by remember { mutableStateOf(false) }

            val rotation = remember { Animatable(0f) }
            val lift = remember { Animatable(0f) }
            val squash = remember { Animatable(1f) }
            val scope = rememberCoroutineScope()
            val liftPx = with(LocalDensity.current) { 80.dp.toPx() }

            fun roll() {
                if (rolling) return
                rolling = true
                val result = rollDice()
                val direction = if (Random.nextBoolean()) 1f else -1f
                scope.launch {
                    rotation.snapTo(0f)
                    listOf(
                        launch {
                            rotation.animateTo(
                                targetValue = direction * 720f,
                                animationSpec = tween(ROLL_DURATION_MS, easing = FastOutSlowInEasing),
                            )
                        },
                        launch {
                            // Toss up, then fall back down.
                            lift.animateTo(1f, tween(ROLL_DURATION_MS * 2 / 5, easing = LinearOutSlowInEasing))
                            lift.animateTo(0f, tween(ROLL_DURATION_MS * 3 / 5, easing = FastOutSlowInEasing))
                        },
                        launch {
                            // Flick through random faces, slowing down as the die lands.
                            var frame = 40L
                            var elapsed = 0L
                            while (elapsed < ROLL_DURATION_MS - frame) {
                                shownFace = rollDice()
                                delay(frame)
                                elapsed += frame
                                frame = (frame * 1.2).toLong()
                            }
                        },
                    ).joinAll()

                    value = result
                    shownFace = result
                    // Squash on impact and spring back.
                    squash.snapTo(0.8f)
                    squash.animateTo(
                        targetValue = 1f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium,
                        ),
                    )
                    rolling = false
                }
            }

            Column(
                modifier = Modifier.fillMaxSize().safeDrawingPadding(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    painter = painterResource(diceDrawable(shownFace)),
                    contentDescription = if (value == 0) {
                        stringResource(Res.string.empty_dice_description)
                    } else {
                        stringResource(Res.string.rolled_description, value)
                    },
                    modifier = Modifier.graphicsLayer {
                        rotationZ = rotation.value
                        translationY = -lift.value * liftPx
                        scaleX = 2f - squash.value
                        scaleY = squash.value
                    },
                )
                Button(onClick = ::roll, enabled = !rolling) {
                    Text(stringResource(Res.string.roll))
                }
            }
        }
    }
}

private fun diceDrawable(value: Int): DrawableResource = when (value) {
    1 -> Res.drawable.dice_1
    2 -> Res.drawable.dice_2
    3 -> Res.drawable.dice_3
    4 -> Res.drawable.dice_4
    5 -> Res.drawable.dice_5
    6 -> Res.drawable.dice_6
    else -> Res.drawable.empty_dice
}
