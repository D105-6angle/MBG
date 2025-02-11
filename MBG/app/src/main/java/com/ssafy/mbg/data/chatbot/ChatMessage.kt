package com.ssafy.mbg.data.chatbot

import java.text.SimpleDateFormat
import java.util.Locale

data class ChatMessage(
    val message : String,
    val isUser : Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val status : MessageStatus = MessageStatus.SENT
) {
    enum class MessageStatus {
        SENDING,
        SENT,
        ERROR
    }

    fun getFormattedTime() : String {
        val dateFormat = SimpleDateFormat("a h:mm", Locale.getDefault())
        return dateFormat.format(timestamp)
    }

    fun getFormattedDate() : String {
        val dateFormat = SimpleDateFormat("yyyy년 M월 d일", Locale.getDefault())
        return dateFormat.format(timestamp)
    }

    companion object {
        fun createSystemMessage(message: String) : ChatMessage {
            return ChatMessage(
                message = message,
                isUser = false,
                status = MessageStatus.SENT
            )
        }
    }
}
