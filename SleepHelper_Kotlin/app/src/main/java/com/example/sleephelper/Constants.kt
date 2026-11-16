package com.example.sleephelper

data class Quality(
    val emoji: String,
    val label: String
)

val qualities = listOf(
    Quality("😫", "Жахливо"),
    Quality("😕", "Погано"),
    Quality("😐", "Нормально"),
    Quality("🙂", "Добре"),
    Quality("😍", "Чудово")
)
