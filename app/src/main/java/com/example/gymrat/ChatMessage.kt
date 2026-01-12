package com.example.gymrat

data class ChatMessage(
    val message: String,
    val isUser: Boolean // True = User, False = AI
)