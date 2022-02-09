package com.livelike.demo.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.livelike.demo.R
import com.livelike.demo.databinding.PinChatMessageBinding
import com.livelike.demo.ui.main.FCVideoView
import com.livelike.demo.viewHolders.ChatTextViewHolder
import com.livelike.demo.viewHolders.ChatVideoViewHolder
import com.livelike.engagementsdk.chat.data.remote.PinMessageInfo


class PinMessageAdapter(private val messageList: ArrayList<PinMessageInfo>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == MSG_TYPE_TEXT) {
            val inflater = LayoutInflater.from(parent.context)
            val bindingObject: PinChatMessageBinding = PinChatMessageBinding.bind(
                inflater.inflate(
                    R.layout.pin_chat_message,
                    parent,
                    false
                )
            )
            return ChatTextViewHolder(bindingObject)
        } else {
            val videoView = FCVideoView(parent.context)
            return ChatVideoViewHolder(videoView)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == MSG_TYPE_TEXT) {
            val textViewHolder = holder as ChatTextViewHolder
            val messagePayload = messageList[position].messagePayload

            textViewHolder.bindingObject.container.clipToOutline = true
            messagePayload?.let {
                textViewHolder.bindingObject.chatMessage.text = it.message
                textViewHolder.bindingObject.chatNickname.text = it.nickname
                it.userPic?.let {
                    Glide.with(holder.itemView.context.applicationContext)
                        .load(it)
                        .placeholder(R.drawable.default_avatar)
                        .error(R.drawable.default_avatar)
                        .into(textViewHolder.bindingObject.imgChatAvatar)
                }

            }
            return
        }


    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (messageList[position].messagePayload?.custom_data == "") {
            true -> MSG_TYPE_TEXT
            false -> MSG_TYPE_VIDEO
        }
    }

    fun addPinMessage(newMessage: PinMessageInfo) {
        messageList.add(newMessage)
        notifyDataSetChanged()
    }

    fun removePinMessage(messageId: String) {
        val index = messageList.indexOfFirst { it.id == messageId }
        if (index != -1) {
            messageList.removeAt(index)
            notifyDataSetChanged()
        }

    }

    fun addPinMessages(pinMessages: ArrayList<PinMessageInfo>) {
        messageList.clear()
        notifyDataSetChanged()
        messageList.addAll(pinMessages)
        notifyDataSetChanged()

    }


    companion object {
        private val MSG_TYPE_TEXT = 0
        private val MSG_TYPE_VIDEO = 1
    }


}