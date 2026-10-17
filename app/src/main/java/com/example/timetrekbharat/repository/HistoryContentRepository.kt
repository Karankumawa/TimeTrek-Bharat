package com.example.timetrekbharat.repository

import android.content.Context
import com.example.timetrekbharat.model.HistoryCategory
import com.example.timetrekbharat.model.HistoryTopicItem
import com.example.timetrekbharat.network.RetrofitClient

object HistoryContentRepository {

    suspend fun fetchTopicsFromSupabase(category: HistoryCategory, context: Context? = null): List<HistoryTopicItem> {
        val folderName = when (category) {
            HistoryCategory.KINGS -> "indian_kings"
            HistoryCategory.POLITICS -> "indian_politics"
            HistoryCategory.ANCIENT -> "ancient_history"
            HistoryCategory.MODERN -> "modern_history"
            else -> "statesimage"
        }

        try {
            val response = RetrofitClient.api.getHistoryTopics("eq.${category.id}")
            if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                val networkTopics = response.body()!!
                return networkTopics.map { topic ->
                    val resolvedUrl = resolveAssetImage(context, folderName, topic.id, topic.title, topic.stateSlug, topic.imageUrl)
                    topic.copy(category = category, imageUrl = resolvedUrl)
                }
            }
        } catch (_: Exception) {
            // Silently handle network errors
        }

        return emptyList()
    }

    private fun resolveAssetImage(
        context: Context?,
        folderName: String,
        topicId: String,
        title: String,
        stateSlug: String?,
        fallbackUrl: String
    ): String {
        if (context != null) {
            try {
                val files = context.assets.list(folderName)?.toList() ?: emptyList()
                val cleanId = topicId.lowercase().replace("-", "_")
                val cleanTitle = title.lowercase().replace(" ", "_").replace("-", "_")

                val matchedFile = files.firstOrNull { fileName ->
                    !fileName.startsWith(".") && (
                        fileName.endsWith(".jpg", ignoreCase = true) ||
                        fileName.endsWith(".jpeg", ignoreCase = true) ||
                        fileName.endsWith(".png", ignoreCase = true) ||
                        fileName.endsWith(".webp", ignoreCase = true)
                    ) && (
                        fileName.substringBeforeLast(".").lowercase().replace("-", "_") == cleanId ||
                        fileName.substringBeforeLast(".").lowercase().replace("-", "_") == cleanTitle ||
                        cleanId.contains(fileName.substringBeforeLast(".").lowercase().replace("-", "_"))
                    )
                }
                if (matchedFile != null) {
                    return "file:///android_asset/$folderName/$matchedFile"
                }
            } catch (_: Exception) {}

            // Fallback check in statesimage directory if linked to a state
            if (!stateSlug.isNullOrBlank()) {
                val cleanStateSlug = stateSlug.lowercase().replace("-", "").replace("_", "")
                val dirsToCheck = listOf(
                    "statesimage",
                    "statesimages",
                    "statesimage/${cleanStateSlug}images",
                    "statesimages/${cleanStateSlug}images",
                    "statesimage/${stateSlug}images",
                    "statesimages/${stateSlug}images"
                )

                for (dir in dirsToCheck) {
                    try {
                        val files = context.assets.list(dir)?.toList() ?: emptyList()
                        val matchedFile = files.firstOrNull { fileName ->
                            !fileName.startsWith(".") && (
                                fileName.substringBeforeLast(".").equals(stateSlug, ignoreCase = true) ||
                                fileName.substringBeforeLast(".").replace("-", "").equals(cleanStateSlug, ignoreCase = true)
                            )
                        }
                        if (matchedFile != null) {
                            return "file:///android_asset/$dir/$matchedFile"
                        }
                    } catch (_: Exception) {}
                }
            }
        }

        return fallbackUrl
    }
}
