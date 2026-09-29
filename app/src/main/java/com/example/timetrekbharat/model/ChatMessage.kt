package com.example.timetrekbharat.model

import java.io.Serializable

data class ChatMessage(
    val id: String = System.currentTimeMillis().toString(),
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
) : Serializable
