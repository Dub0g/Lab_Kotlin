package com.example.sleephelper.models

import org.json.JSONObject

data class SleepEntry(
    val id: String = "",
    val date: String,
    val bedtime: String,
    val wake: String,
    val durationMin: Int,
    val quality: Int,
    val notes: String = "",
    val remoteId: Int? = null,
    val synced: Boolean = false
) {
    fun toJson(): JSONObject {
        return JSONObject().apply {
            put("date", date)
            put("bedtime", bedtime)
            put("wake", wake)
            put("durationMin", durationMin)
            put("quality", quality)
            put("notes", notes)
        }
    }

    companion object {
        fun fromJson(json: JSONObject): SleepEntry {
            return SleepEntry(
                id = json.optString("id", ""),
                date = json.optString("date", ""),
                bedtime = json.optString("bedtime", ""),
                wake = json.optString("wake", ""),
                durationMin = json.optInt("durationMin", 0),
                quality = json.optInt("quality", 0),
                notes = json.optString("notes", ""),
                remoteId = if (json.has("remoteId") && !json.isNull("remoteId")) json.optInt("remoteId") else null,
                synced = json.optBoolean("synced", false)
            )
        }
    }
}
