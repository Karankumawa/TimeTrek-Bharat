package com.example.timetrekbharat.db

import androidx.room.TypeConverter
import com.example.timetrekbharat.model.CulturalHeritage
import com.example.timetrekbharat.model.FamousMonument
import com.example.timetrekbharat.model.StateSymbols
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

    @TypeConverter
    fun fromStateSymbols(symbols: StateSymbols?): String? {
        return if (symbols == null) null else gson.toJson(symbols)
    }

    @TypeConverter
    fun toStateSymbols(data: String?): StateSymbols? {
        if (data == null) return null
        return gson.fromJson(data, StateSymbols::class.java)
    }

    @TypeConverter
    fun fromCulturalHeritage(heritage: CulturalHeritage?): String? {
        return if (heritage == null) null else gson.toJson(heritage)
    }

    @TypeConverter
    fun toCulturalHeritage(data: String?): CulturalHeritage? {
        if (data == null) return null
        return gson.fromJson(data, CulturalHeritage::class.java)
    }

    @TypeConverter
    fun fromFamousMonumentsList(list: List<FamousMonument>?): String? {
        return if (list == null) null else gson.toJson(list)
    }

    @TypeConverter
    fun toFamousMonumentsList(data: String?): List<FamousMonument> {
        if (data == null) return ArrayList()
        val listType = object : TypeToken<List<FamousMonument>>() {}.type
        return gson.fromJson(data, listType)
    }
}
