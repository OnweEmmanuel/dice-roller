package com.example.diceroller

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DiceTest {
    @Test
    fun rollsAreAlwaysBetweenOneAndSix() {
        val random = Random(42)
        repeat(1_000) {
            val value = rollDice(random)
            assertTrue(value in 1..DICE_SIDES, "Rolled $value")
        }
    }

    @Test
    fun everyFaceComesUp() {
        val random = Random(7)
        val seen = List(1_000) { rollDice(random) }.toSet()
        assertEquals((1..DICE_SIDES).toSet(), seen)
    }
}
