package com.example.sleephelper.utils

import java.time.LocalTime

const val CYCLE_MIN = 90
const val FALL_ASLEEP_MIN = 15
const val OPTIMAL_CYCLES = 5

data class CycleResult(
    val cycles: Int,
    val sleepMin: Int,
    val time: LocalTime
)

private fun shift(time: LocalTime, minutes: Int): LocalTime =
    time.plusMinutes(minutes.toLong())

fun bedtimesForWake(
    wake: LocalTime,
    cycles: List<Int> = listOf(6, 5, 4)
): List<CycleResult> {
    return cycles.map { n ->
        CycleResult(n, n * CYCLE_MIN, shift(wake, -(n * CYCLE_MIN + FALL_ASLEEP_MIN)))
    }
}

fun wakeTimesForBed(
    bed: LocalTime,
    cycles: List<Int> = listOf(4, 5, 6)
): List<CycleResult> {
    return cycles.map { n ->
        CycleResult(n, n * CYCLE_MIN, shift(bed, FALL_ASLEEP_MIN + n * CYCLE_MIN))
    }
}

fun cyclesWord(n: Int): String {
    val last = n % 10
    val lastTwo = n % 100
    if (lastTwo in 11..14) return "циклів"
    if (last == 1) return "цикл"
    if (last in 2..4) return "цикли"
    return "циклів"
}
