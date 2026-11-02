package com.example.diceroller

import kotlin.random.Random

const val DICE_SIDES = 6

/** Rolls a six-sided die and returns a value from 1 to [DICE_SIDES]. */
fun rollDice(random: Random = Random): Int = random.nextInt(1, DICE_SIDES + 1)
