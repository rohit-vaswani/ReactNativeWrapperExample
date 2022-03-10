package com.livelike.demo.adapters

import android.content.Context
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.core.view.children
import androidx.core.view.forEach
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.BaseRequestOptions
import com.bumptech.glide.request.RequestOptions
import com.facebook.react.uimanager.ThemedReactContext
import com.livelike.demo.R
import com.livelike.demo.databinding.PinHybridMessageBinding
import com.livelike.engagementsdk.chat.data.remote.PinMessageInfo
import com.livelike.engagementsdk.publicapis.LiveLikeChatMessage
import org.json.JSONObject


class PinMessageAdapter() {

    private val messageList: ArrayList<PinMessageInfo> = arrayListOf()
    var parentView: RelativeLayout? = null
    lateinit var pinMessageHandler: PinMessageActionHandler

    interface PinMessageActionHandler {
        fun onVideoPlayed(videoUrl: String)
        fun onRemoveAllPinMessages()
    }


    private fun bindHybridMessage(
        bindingObject: PinHybridMessageBinding,
        pinMessage: PinMessageInfo
    ) {

        val messagePayload = pinMessage.messagePayload
        val isChatMessage: Boolean = isVideoMessage(messagePayload).not()
        val pinView = bindingObject.root
        bindingObject.container.clipToOutline = true

        // Avatar + Video Thumbnail
        bindingObject.imgChatAvatar.visibility = when (isChatMessage) {
            true -> View.VISIBLE
            else -> View.GONE
        }
        bindingObject.imgVideoThumbnailContainer.clipToOutline = true
        bindingObject.imgVideoThumbnail.clipToOutline = true
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
        bindingObject.chatMessage.text = message


        val appContext: Context = pinView.context.applicationContext
        if (isChatMessage) {
            updateImageResource(
                appContext,
                bindingObject.imgChatAvatar,
                messagePayload?.userPic,
                R.drawable.default_avatar,
                getAvatarOptions()
            )
        } else {
            updateImageResource(
                appContext,
                bindingObject.imgVideoThumbnail,
                getCustomDataProp("videoThumbnail", messagePayload),
                R.drawable.default_video_thumbnail,
                RequestOptions()
            )
        }

        pinMessage.id?.let {
            registerListener(bindingObject, it)
        }
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
            pinMessageHandler.onRemoveAllPinMessages()
        }

        bindingObject.container.setOnClickListener {
            if (isVideoMessage) {
                getCustomDataProp("url", messageDetails.messagePayload)?.let {
                    pinMessageHandler.onVideoPlayed(it)
                }
            }

            Handler().postDelayed({
                removePinMessage(messageId)
            }, 1000)

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

    fun setup(view: RelativeLayout) {
        this.parentView = view
    }

    fun clear() {
        this.messageList.clear()
        this.parentView = null

    }

    fun addPinMessage(
        pinMessage: PinMessageInfo,
        parentContext: ThemedReactContext
    ) {

        val pinnedBindingObject = PinHybridMessageBinding.bind(
            LayoutInflater.from(parentContext)
                .inflate(R.layout.pin_hybrid_message, parentView, false)
        )
        val pinnedView = pinnedBindingObject.root
        pinnedView.setTag(pinMessage.id)
        messageList.add(pinMessage)
        parentView?.addView(pinnedView)
        bindHybridMessage(pinnedBindingObject, pinMessage)

    }


    fun removePinMessage(messageId: String?) {
        val index = messageList.indexOfFirst { it.id == messageId }
        if (index != -1) {
            messageList.removeAt(index)
        }
        parentView?.children?.forEach {
            val thisMessageId = it.tag as String
            if(thisMessageId == messageId) {
                parentView?.removeView(it)
            }
        }
    }

    private fun getDetailedMessageBy(messageId: String): PinMessageInfo {
        val index = messageList.indexOfFirst { it.id == messageId }
        return messageList[index]
    }

    private fun removeAllPinMessages() {
        this.messageList.clear()
        this.parentView?.removeAllViews()
    }

}