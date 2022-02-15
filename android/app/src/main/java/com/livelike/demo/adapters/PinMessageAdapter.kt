package com.livelike.demo.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.livelike.demo.R
import com.livelike.demo.databinding.PinChatMessageBinding
import com.livelike.demo.ui.main.PinVideoMessageView
import com.livelike.demo.viewHolders.PinnedTextMessageHolder
import com.livelike.demo.viewHolders.PinnedVideoMsgHolder
import com.livelike.engagementsdk.chat.data.remote.PinMessageInfo
import com.livelike.engagementsdk.publicapis.LiveLikeChatMessage
import org.json.JSONObject


class PinMessageAdapter(private val messageList: ArrayList<PinMessageInfo>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


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
            return PinnedTextMessageHolder(bindingObject)
        } else {
            val videoView = LayoutInflater.from(parent.context)
                .inflate(R.layout.pin_video_message, parent, false) as PinVideoMessageView
            videoView.initViews()
            return PinnedVideoMsgHolder(videoView)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (getItemViewType(position) == MSG_TYPE_TEXT) {
            val textViewHolder = holder as PinnedTextMessageHolder
            val messagePayload = messageList[position].messagePayload
            bindTextMessage(textViewHolder, messagePayload)
        } else {
            val videoViewHolder = holder as PinnedVideoMsgHolder
            val messagePayload = messageList[position].messagePayload
            bindVideoMessage(videoViewHolder, messagePayload)
        }

    }

    private fun bindTextMessage(
        textViewHolder: PinnedTextMessageHolder,
        messagePayload: LiveLikeChatMessage?
    ) {

        textViewHolder.bindingObject.container.clipToOutline = true
        messagePayload?.let {
            textViewHolder.bindingObject.chatMessage.text = it.message
            textViewHolder.bindingObject.chatNickname.text = it.nickname
            it.userPic?.let {
                Glide.with(textViewHolder.itemView.context.applicationContext)
                    .load(it)
                    .placeholder(R.drawable.default_avatar)
                    .error(R.drawable.default_avatar)
                    .into(textViewHolder.bindingObject.imgChatAvatar)
            }
        }
    }

    private fun bindVideoMessage(
        videoViewHolder: PinnedVideoMsgHolder,
        messagePayload: LiveLikeChatMessage?
    ) {

        val videoMessageView = videoViewHolder.itemView as PinVideoMessageView
        val imgChatAvatar = videoMessageView.imgChatAvatar


        messagePayload?.custom_data?.let {



            val jsonObject = JSONObject(it)
            val url = jsonObject.get("url").toString()
            val nickName = jsonObject.get("nickName").toString()
            val userPic = jsonObject.get("userPic").toString()


            try{
                val videoThumbnail = jsonObject.get("videoThumbnail").toString()
                videoThumbnail.let {
                    videoMessageView.setVideoThumbnail(it)
                }
            }catch (e: Exception) {}


            url?.let {
                videoMessageView.videoUrl = it
            }
            nickName?.let {
                videoMessageView.chatNickname.text = it
            }

            // Set Avatar
            imgChatAvatar?.let {
                Glide.with(videoViewHolder.itemView.context.applicationContext)
                    .load(userPic)
                    .placeholder(R.drawable.default_avatar)
                    .error(R.drawable.default_avatar)
                    .into(it)
            }
        }

    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    private fun isVideoMessage(messagePayload: LiveLikeChatMessage?): Boolean {

        if (messagePayload?.custom_data.isNullOrEmpty()) {
            return false
        }

        messagePayload?.custom_data?.let {
            val jsonObject = JSONObject(it)
            val messageType = jsonObject.get("messageType").toString()
            return messageType.toLowerCase() == "video"
        }

        return false

    }

    override fun getItemViewType(position: Int): Int {
        return when (isVideoMessage(messageList[position].messagePayload)) {
            true -> MSG_TYPE_VIDEO
            false -> MSG_TYPE_TEXT
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