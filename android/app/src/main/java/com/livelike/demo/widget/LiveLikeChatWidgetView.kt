package com.livelike.demo.widget

import android.content.res.Resources
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.Choreographer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.facebook.react.bridge.*
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.events.RCTEventEmitter
import com.livelike.demo.R
import com.livelike.demo.adapters.PinMessageAdapter
import com.livelike.demo.databinding.FcChatViewBinding
import com.livelike.demo.ui.main.FCVideoView
import com.livelike.engagementsdk.MessageListener
import com.livelike.engagementsdk.chat.ChatView
import com.livelike.engagementsdk.chat.ChatViewDelegate
import com.livelike.engagementsdk.chat.ChatViewThemeAttributes
import com.livelike.engagementsdk.chat.LiveLikeChatSession
import com.livelike.engagementsdk.chat.data.remote.PinMessageInfo
import com.livelike.engagementsdk.publicapis.ChatMessageType
import com.livelike.engagementsdk.publicapis.LiveLikeCallback
import com.livelike.engagementsdk.publicapis.LiveLikeChatMessage
import org.json.JSONObject


class LiveLikeChatWidgetView(
    val context: ThemedReactContext,
    val applicationContext: ReactApplicationContext
) : ConstraintLayout(context), LifecycleEventListener {

    private var renderWidget = true
    lateinit var chatView: ChatView;
    private var chatViewBinding: FcChatViewBinding? = null
    var fallback: Choreographer.FrameCallback;
    var chatSession: LiveLikeChatSession? = null
    var userAvatarUrl = ""
    private var pinMessageAdapter = PinMessageAdapter()


    init {

        this.applicationContext.addLifecycleEventListener(this)
        this.fallback = Choreographer.FrameCallback() {
            manuallyLayoutChildren();
            viewTreeObserver.dispatchOnGlobalLayout();
            if (renderWidget) {
                Choreographer.getInstance().postFrameCallback(this!!.fallback)
            }
        }
        Choreographer.getInstance().postFrameCallback(fallback)
        val inflater: LayoutInflater = LayoutInflater.from(context)
        chatViewBinding = FcChatViewBinding.bind(inflater.inflate(R.layout.fc_chat_view, null))
        registerPinMessagesHandler()
        chatViewBinding?.let {
            chatView = it.chatView
            addView(it.root)
            configureChatView()
        }

    }

    private fun registerPinMessagesHandler() {
        pinMessageAdapter.pinMessageHandler = object : PinMessageAdapter.PinMessageActionHandler {
            override fun onVideoPlayed(videoUrl: String) {
                val params = Arguments.createMap()
                params.putString("videoUrl", videoUrl)
                sendEvent(EVENT_VIDEO_PLAYED, params)
            }

        }
    }

    override fun onHostResume() {
        chatSession?.resume()
    }

    override fun onHostPause() {
        chatSession?.pause()
    }

    override fun onHostDestroy() {
    }

    fun updateChatSession(chatSession: LiveLikeChatSession?) {
        this.chatSession = chatSession
        this.chatSession?.allowDiscardOwnPublishedMessageInSubscription = false
    }

    private fun configureChatView() {
        chatView.isChatInputVisible = false
        chatView.allowMediaFromKeyboard = false
    }

    fun scrollToBottom() {
        chatView.scrollChatToBottom()
    }

    private fun manuallyLayoutChildren() {
        for (i in 0 until getChildCount()) {
            var child = getChildAt(i);
            child.measure(
                MeasureSpec.makeMeasureSpec(getMeasuredWidth(), MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(getMeasuredHeight(), MeasureSpec.EXACTLY)
            );
            child.layout(0, 0, child.getMeasuredWidth(), child.getMeasuredHeight());
        }
    }


    fun setupChat(chatRoomId: String) {
        setSessionToChatView()
        connectToChatRoom(chatRoomId)
        setUserAvatar()
        registerMessageListener()
        registerVideoMessageHandler()
    }


    fun setAvatar(avatarUrl: String?) {
        avatarUrl?.let {
            this.userAvatarUrl = it
            chatSession?.shouldDisplayAvatar = true
            chatSession?.avatarUrl = it
        }
    }

    private fun setUserAvatar() {
        if (this.userAvatarUrl == "") {
            return
        }

        chatSession?.shouldDisplayAvatar = true
        chatSession?.avatarUrl = this.userAvatarUrl
    }


    private fun connectToChatRoom(chatRoomId: String) {
        chatSession?.connectToChatRoom(
            chatRoomId,
            callback = object : LiveLikeCallback<Unit>() {
                override fun onResponse(result: Unit?, error: String?) {
                    Handler(Looper.getMainLooper()).post(Runnable {
                        val params = Arguments.createMap()
                        sendEvent(EVENT_CHAT_ROOM_CONNECTED, params)
                    })
                }
            })
    }

    private fun setSessionToChatView() {
        if (chatSession != null) {
            chatView.setSession(chatSession!!)
        }
    }


    private fun registerVideoMessageHandler() {
        chatView.chatViewDelegate = object : ChatViewDelegate {
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: ChatMessageType
            ): RecyclerView.ViewHolder {
                return FCVideoViewHolder(FCVideoView(parent.context))
            }

            override fun onBindViewHolder(
                holder: RecyclerView.ViewHolder,
                liveLikeChatMessage: LiveLikeChatMessage,
                chatViewThemeAttributes: ChatViewThemeAttributes,
                showChatAvatar: Boolean
            ) {


                registerVideoEvents(holder.itemView as FCVideoView)

                chatViewThemeAttributes.apply {

                    (holder.itemView as FCVideoView)._binding?.let {

                        // Setting nickName
                        it.chatNickname.text = liveLikeChatMessage.nickname
                        it.chatNickname.setTextSize(
                            TypedValue.COMPLEX_UNIT_PX,
                            chatUserNameTextSize
                        )
                        it.chatNickname.isAllCaps = chatUserNameTextAllCaps


                        // Setting Video Title
                        val videoTitle = getCustomDataProp("title", liveLikeChatMessage)
                        it.videoTitle.text = videoTitle

                        // User Avatar
                        it.imgChatAvatar.visibility = when (showChatAvatar) {
                            true -> View.VISIBLE
                            else -> View.GONE
                        }
                        val avatarLayoutParams = LinearLayout.LayoutParams(
                            chatAvatarWidth,
                            chatAvatarHeight
                        )
                        avatarLayoutParams.gravity = chatAvatarGravity
                        it.imgChatAvatar.layoutParams = avatarLayoutParams
                        avatarLayoutParams.setMargins(
                            avatarLayoutParams.leftMargin,
                            avatarLayoutParams.topMargin + dpToPx(8),
                            avatarLayoutParams.rightMargin,
                            avatarLayoutParams.bottomMargin
                        )
                        val options = RequestOptions()
                        if (chatAvatarCircle) {
                            options.optionalCircleCrop()
                        }
                        if (chatAvatarRadius > 0) {
                            options.transform(
                                CenterCrop(),
                                RoundedCorners(chatAvatarRadius)
                            )
                        }
                        val userPic: String? = getCustomDataProp("userPic", liveLikeChatMessage)
                        Glide.with(holder.itemView.context.applicationContext)
                            .load(userPic)
                            .placeholder(chatUserPicDrawable)
                            .error(chatUserPicDrawable)
                            .into(it.imgChatAvatar)


                        // Handle Chat background
                        val chatBackgroundLayoutParams =
                            it.chatBackground.layoutParams as ConstraintLayout.LayoutParams
                        chatBackgroundLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                        it.chatBackground.layoutParams = chatBackgroundLayoutParams

                        // Chat Bubble background
                        val chatBubbleLayoutParams: LinearLayout.LayoutParams =
                            it.chatBubbleBackground.layoutParams as LinearLayout.LayoutParams
                        it.chatBubbleBackground.setPadding(
                            chatBubblePaddingLeft,
                            chatBubblePaddingTop,
                            chatBubblePaddingRight,
                            chatBubblePaddingBottom + dpToPx(6)
                        )
//                        chatBubbleLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                        it.chatBubbleBackground.layoutParams = chatBubbleLayoutParams
                        it.chatBubbleBackground.clipToOutline = true

                        // Video Thumbnail
                        val url: String? = getCustomDataProp("url", liveLikeChatMessage)
                        url?.let {
                            val videoViewHolder = (holder as FCVideoViewHolder)
                            val fcVideoView = videoViewHolder.itemView as FCVideoView
                            try {
                                fcVideoView.setVideoThumbnail(
                                    getCustomDataProp(
                                        "videoThumbnail",
                                        liveLikeChatMessage
                                    )
                                )
                            } catch (e: Exception) {
                            }
                            fcVideoView.videoUrl = it
                        }

                    }
                }
            }
        }

    }

    private fun registerVideoEvents(videoView: FCVideoView) {

        videoView.videoEventHandler = object : FCVideoView.IVideoEventHandler {
            override fun onAskInfluencer() {
                sendEvent(EVENT_ASK_INFLUENCER, Arguments.createMap())
            }

            override fun onVideoPlayed(videoUrl: String) {
                val map = Arguments.createMap()
                map.putString("videoUrl", videoUrl)
                sendEvent(EVENT_VIDEO_PLAYED, map)
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

    class FCVideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}


    private fun registerMessageListener() {

        chatSession?.setMessageListener(object : MessageListener {
            override fun onDeleteMessage(messageId: String) {}

            override fun onHistoryMessage(messages: List<LiveLikeChatMessage>) {}

            override fun onNewMessage(message: LiveLikeChatMessage) {}

            override fun onPinMessage(message: PinMessageInfo) {
                Handler(Looper.getMainLooper()).post(Runnable {
                    pinMessageAdapter.addPinMessage(message, context)
                })
            }

            override fun onUnPinMessage(pinMessageId: String) {
                Handler(Looper.getMainLooper()).post(Runnable {
                    pinMessageAdapter.removePinMessage(pinMessageId)
                })
            }
        })
    }


    fun sendChatMessage(message: String) {
        chatSession?.sendChatMessage(
            message,
            null,
            null,
            null,
            object : LiveLikeCallback<LiveLikeChatMessage>() {
                override fun onResponse(result: LiveLikeChatMessage?, error: String?) {
                    val params = Arguments.createMap()
                    params.putString("message", message)
                    params.putBoolean("isSuccess", error.isNullOrEmpty())
                    sendEvent(CHAT_MESSAGE_SENT, params)
                }
            })

    }


    fun dpToPx(dp: Int): Int {
        val scale = Resources.getSystem().displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }


    fun handleHistoricalPinMessages(pinnedMessages: List<PinMessageInfo>) {
        chatViewBinding?.pinnedMessageList?.let {
            pinMessageAdapter.setup(it)
        }
        pinnedMessages.forEach {
            pinMessageAdapter.addPinMessage(it, context)
        }
    }

    fun sendEvent(
        eventName: String,
        params: WritableMap?
    ) {
        val reactContext = this.getContext() as ReactContext;
        reactContext.getJSModule(RCTEventEmitter::class.java)
            .receiveEvent(this.getId(), eventName, params)
    }

    fun destroyChatSession() {
        if(chatSession != null) {
            chatView.clearSession()
            chatSession?.close()
            chatSession = null
            pinMessageAdapter.clear()
        }
    }


    companion object {
        const val CHAT_MESSAGE_SENT = "onChatMessageSent"
        const val EVENT_VIDEO_PLAYED = "onVideoPlayed"
        const val EVENT_ASK_INFLUENCER = "onAskInfluencer"
        const val EVENT_CHAT_ROOM_CONNECTED = "onChatRoomConnected"
    }
}
