package com.example.timetrekbharat.db

import androidx.room.TypeConverter
import com.example.timetrekbharat.model.TimelineEntry
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {

    private val gson = Gson()

    @TypeConverter
    fun fromTimelineList(list: List<TimelineEntry>?): String? {
        return if (list == null) null else gson.toJson(list)
    }

    @TypeConverter
    fun toTimelineList(data: String?): List<TimelineEntry> {
        if (data == null) return ArrayList()
        val listType = object : TypeToken<List<TimelineEntry>>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun fromStringList(list: List<String>?): String? {
        return if (list == null) null else gson.toJson(list)
    }

    @TypeConverter
    fun toStringList(data: String?): List<String> {
        if (data == null) return ArrayList()
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(data, listType)
    }
}
