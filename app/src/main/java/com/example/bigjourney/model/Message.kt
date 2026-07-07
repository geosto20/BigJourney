package com.example.bigjourney.model

data class Message(
    val senderId: String = "",
    val message: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

data class Chat(
    val chatId: String = "",
    val lastMessage: String = "",
    val timestamp: Long = 0L,
    val users: Map<String, Boolean> = emptyMap()
)

