package com.example.timetrekbharat.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.example.timetrekbharat.R
import com.example.timetrekbharat.databinding.ItemChatMessageBinding
import com.example.timetrekbharat.model.ChatMessage

class ChatAdapter : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    private val messages = ArrayList<ChatMessage>()

    fun setMessages(list: List<ChatMessage>) {
        messages.clear()
        messages.addAll(list)
        notifyDataSetChanged()
    }

    fun addMessage(message: ChatMessage) {
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val binding = ItemChatMessageBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ChatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(messages[position])
    }

    override fun getItemCount(): Int = messages.size

    inner class ChatViewHolder(private val binding: ItemChatMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(msg: ChatMessage) {
            if (msg.isUser) {
                binding.containerUser.visibility = View.VISIBLE
                binding.containerAi.visibility = View.GONE
                binding.tvUserText.text = msg.text
            } else {
                binding.containerUser.visibility = View.GONE
                binding.containerAi.visibility = View.VISIBLE
                binding.tvAiText.text = msg.text
            }

            val animation = AnimationUtils.loadAnimation(itemView.context, R.anim.item_animation_fade_in)
            itemView.startAnimation(animation)
        }
    }
}
