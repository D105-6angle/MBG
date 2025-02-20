package com.ssafy.mbg.ui.chatbot

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.mbg.R
import com.ssafy.mbg.data.chatbot.ChatMessage

class UserMessageViewHolders(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val messageText : TextView = itemView.findViewById(R.id.messageText)

    fun bind(message: ChatMessage) {
        messageText.text = message.message
    }
}

class BotMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val messageText: TextView = itemView.findViewById(R.id.messageText)
    private val botIcon: ImageView = itemView.findViewById(R.id.botIcon)

    fun bind(message: ChatMessage) {
        messageText.text = message.message

        // 봇 아이콘에 애니메이션을 추가할 수 있습니다
        botIcon.setOnClickListener {
            it.animate()
                .scaleX(1.2f)
                .scaleY(1.2f)
                .setDuration(100)
                .withEndAction {
                    it.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(100)
                        .start()
                }
                .start()
        }
    }
}
