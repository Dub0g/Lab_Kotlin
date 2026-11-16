package com.example.sleephelper.utils

import java.time.LocalDate
import java.time.LocalTime
import java.util.Locale

private fun pad(n: Int): String = String.format(Locale.US, "%02d", n)

fun formatTime(time: LocalTime): String = "${pad(time.hour)}:${pad(time.minute)}"

fun calcDurationMinutes(bed: LocalTime, wake: LocalTime): Int {
    val bedMin = bed.hour * 60 + bed.minute
    val wakeMin = wake.hour * 60 + wake.minute
    var diff = wakeMin - bedMin
    if (diff <= 0) diff += 24 * 60
    return diff
}

fun formatDuration(minutes: Int): String {
    val h = minutes / 60
    val m = minutes % 60
    if (h == 0) return "$m хв"
    if (m == 0) return "$h год"
    return "$h год $m хв"
}

fun todayKey(): String {
    val now = LocalDate.now()
    return "${now.year}-${pad(now.monthValue)}-${pad(now.dayOfMonth)}"
}

fun makeTime(h: Int, m: Int): LocalTime = LocalTime.of(h, m)
