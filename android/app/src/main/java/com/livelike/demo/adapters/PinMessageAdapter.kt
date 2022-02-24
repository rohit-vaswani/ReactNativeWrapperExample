package com.livelike.demo.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.BaseRequestOptions
import com.bumptech.glide.request.RequestOptions
import com.livelike.demo.R
import com.livelike.demo.databinding.PinHybridMessageBinding
import com.livelike.demo.ui.main.PinVideoMessageView
import com.livelike.demo.viewHolders.PinnedTextMessageHolder
import com.livelike.demo.viewHolders.PinnedVideoMsgHolder
import com.livelike.engagementsdk.chat.data.remote.PinMessageInfo
import com.livelike.engagementsdk.publicapis.LiveLikeChatMessage
import org.json.JSONObject


class PinMessageAdapter(private val messageList: ArrayList<PinMessageInfo>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    lateinit var pinMessageHandler: PinMessageActionHandler

    interface PinMessageActionHandler {
        fun onVideoPlayed(videoUrl: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val bindingObject: PinHybridMessageBinding = PinHybridMessageBinding.bind(
            inflater.inflate(
                R.layout.pin_hybrid_message,
                parent,
                false
            )
        )
        return PinnedTextMessageHolder(bindingObject)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val textViewHolder = holder as PinnedTextMessageHolder
        val messagePayload = messageList[position].messagePayload
        messageList[position].id?.let {
            textViewHolder.messageId = it
        }
        bindHybridMessage(textViewHolder, position, messagePayload)
    }

    private fun bindHybridMessage(
        textViewHolder: PinnedTextMessageHolder,
        position: Int,
        messagePayload: LiveLikeChatMessage?
    ) {

        val bindingObject = textViewHolder.bindingObject
        val isChatMessage: Boolean = getItemViewType(position) == MSG_TYPE_TEXT
        textViewHolder.bindingObject.container.clipToOutline = true

        // Avatar + Video Thumbnail
        bindingObject.imgChatAvatar.visibility = when (isChatMessage) {
            true -> View.VISIBLE
            else -> View.GONE
        }
        textViewHolder.bindingObject.imgVideoThumbnailContainer.clipToOutline = true
        textViewHolder.bindingObject.imgVideoThumbnail.clipToOutline = true
        bindingObject.imgVideoThumbnailContainer.visibility = when (isChatMessage) {
            true -> View.GONE
            else -> View.VISIBLE
        }

        // Text message
        var message: String? = ""
        try {
            message = if (isChatMessage) messagePayload?.message else getCustomDataProp(
                "title",
                messagePayload,
            )
        } catch (e: java.lang.Exception) {
        }
        textViewHolder.bindingObject.chatMessage.text = message


        val appContext: Context = textViewHolder.itemView.context.applicationContext
        if (isChatMessage) {
            updateImageResource(
                appContext,
                textViewHolder.bindingObject.imgChatAvatar,
                messagePayload?.userPic,
                R.drawable.default_avatar,
                getAvatarOptions()
            )
        } else {
            updateImageResource(
                appContext,
                textViewHolder.bindingObject.imgVideoThumbnail,
                getCustomDataProp("videoThumbnail", messagePayload),
                R.drawable.default_video_thumbnail,
                RequestOptions()
            )
        }

        registerListener(bindingObject, textViewHolder.messageId)
    }

    private fun getAvatarOptions(): BaseRequestOptions<RequestOptions> {
        val options = RequestOptions()
        options.optionalCircleCrop()
        options.transform(
            CenterCrop(),
            RoundedCorners(10)
        )
        return options

    }

    private fun registerListener(bindingObject: PinHybridMessageBinding, messageId: String) {


        val messageDetails = getDetailedMessageBy(messageId)
        val isVideoMessage = isVideoMessage(messageDetails.messagePayload)

        bindingObject.closeBtnContainer.setOnClickListener {
            removeAllPinMessages()
        }


        bindingObject.container.setOnClickListener {
            if (isVideoMessage) {
                getCustomDataProp("url", messageDetails.messagePayload)?.let {
                    pinMessageHandler.onVideoPlayed(it)
                    removePinMessage(messageId)
                }
            } else {
                removePinMessage(messageId)
            }
        }


    }

    private fun getCustomDataProp(key: String, messagePayload: LiveLikeChatMessage?): String? {

        try {
            messagePayload?.custom_data?.let {
                val jsonObject = JSONObject(it)
                return jsonObject.get(key).toString()
            }
        } catch (e: java.lang.Exception) {

        }
        return null
    }

    private fun updateImageResource(
        appContext: Context,
        targetNode: ImageView,
        targetSource: String?,
        defaultSource: Int,
        options: BaseRequestOptions<RequestOptions>
    ) {
        targetSource?.let {
            Glide.with(appContext)
                .load(it)
                .apply(options)
                .placeholder(defaultSource)
                .error(defaultSource)
                .into(targetNode)
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

            try {
                val videoThumbnail = jsonObject.get("videoThumbnail").toString()
                videoThumbnail.let {
                    videoMessageView.setVideoThumbnail(it)
                }
            } catch (e: Exception) {
            }


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
        addMessageAtIndex(newMessage)
        notifyDataSetChanged()
    }

    fun removePinMessage(messageId: String?) {
        val index = messageList.indexOfFirst { it.id == messageId }
        if (index != -1) {
            messageList.removeAt(index)
            notifyDataSetChanged()
        }
    }

    private fun getDetailedMessageBy(messageId: String): PinMessageInfo {
        val index = messageList.indexOfFirst { it.id == messageId }
        return messageList[index]
    }

    private fun addMessageAtIndex(pinMessage: PinMessageInfo) {
        messageList.add(0, pinMessage)
    }

    fun addPinMessages(pinMessages: ArrayList<PinMessageInfo>) {
        messageList.clear()
        for (i in pinMessages.indices) {
            addMessageAtIndex(pinMessages[i])
        }
        notifyDataSetChanged()

    }

    private fun removeAllPinMessages() {
        this.messageList.clear()
        notifyDataSetChanged()
    }


    companion object {
        private val MSG_TYPE_TEXT = 0
        private val MSG_TYPE_VIDEO = 1
    }


}