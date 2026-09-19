package com.quickcopy.app.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

data class HistoryItem(
    val id: String,
    val content: String,
    val original: String,
    val paramsRemoved: Int,
    val timestamp: Long
)

class ClipboardRepository private constructor(context: Context) {

    private val prefs = context.getSharedPreferences("quickcopy_history", Context.MODE_PRIVATE)

    fun getAll(): List<HistoryItem> {
        val jsonStr = prefs.getString("items", "[]") ?: "[]"
        val items = mutableListOf<HistoryItem>()
        try {
            val array = JSONArray(jsonStr)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                items.add(
                    HistoryItem(
                        id = obj.getString("id"),
                        content = obj.getString("content"),
                        original = obj.optString("original", obj.getString("content")),
                        paramsRemoved = obj.optInt("paramsRemoved", 0),
                        timestamp = obj.getLong("timestamp")
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return items.sortedByDescending { it.timestamp }
    }

    fun addEntry(content: String, original: String, paramsRemoved: Int) {
        val current = getAll().toMutableList()
        val newItem = HistoryItem(
            id = System.currentTimeMillis().toString(),
            content = content,
            original = original,
            paramsRemoved = paramsRemoved,
            timestamp = System.currentTimeMillis()
        )
        current.add(0, newItem)

        // Keep last 100 items
        val trimmed = if (current.size > 100) current.take(100) else current

        val array = JSONArray()
        for (item in trimmed) {
            val obj = JSONObject()
            obj.put("id", item.id)
            obj.put("content", item.content)
            obj.put("original", item.original)
            obj.put("paramsRemoved", item.paramsRemoved)
            obj.put("timestamp", item.timestamp)
            array.put(obj)
        }

        prefs.edit().putString("items", array.toString()).apply()
    }

    fun clearAll() {
        prefs.edit().remove("items").apply()
    }

    companion object {
        @Volatile
        private var instance: ClipboardRepository? = null

        fun getInstance(context: Context): ClipboardRepository {
            return instance ?: synchronized(this) {
                instance ?: ClipboardRepository(context.applicationContext).also { instance = it }
            }
        }
    }
}