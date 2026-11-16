package com.example.sleephelper.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sleephelper.models.SleepEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

private class ServerException(message: String) : Exception(message)

class SleepViewModel : ViewModel() {
    private val _entries = mutableStateListOf<SleepEntry>()
    val entries: List<SleepEntry> get() = _entries

    var saving by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    fun clearError() {
        error = null
    }

    fun addEntry(draft: SleepEntry, onComplete: (String?) -> Unit) {
        viewModelScope.launch {
            saving = true
            error = null
            val id = System.currentTimeMillis().toString()

            try {
                val remoteId = withContext(Dispatchers.IO) {
                    val url = URL("https://jsonplaceholder.typicode.com/posts")
                    val conn = (url.openConnection() as HttpURLConnection).apply {
                        requestMethod = "POST"
                        setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                        connectTimeout = 15_000
                        readTimeout = 15_000
                        doOutput = true
                    }

                    val bodyJson = JSONObject().apply {
                        put("title", "sleep-entry-${draft.date}")
                        put("body", draft.toJson().toString())
                        put("userId", 1)
                    }

                    conn.outputStream.use { os ->
                        os.write(bodyJson.toString().toByteArray(Charsets.UTF_8))
                    }

                    val code = conn.responseCode
                    if (code !in 200..299) {
                        throw ServerException("Помилка сервера: $code")
                    }

                    val responseText = conn.inputStream.bufferedReader().use { it.readText() }
                    val data = JSONObject(responseText)
                    if (data.has("id")) data.optInt("id") else null
                }

                _entries.add(0, draft.copy(id = id, remoteId = remoteId, synced = true))
            } catch (e: Exception) {
                error = if (e is ServerException) e.message else "Немає з’єднання"
                _entries.add(0, draft.copy(id = id, synced = false))
            } finally {
                saving = false
                onComplete(error)
            }
        }
    }
}
