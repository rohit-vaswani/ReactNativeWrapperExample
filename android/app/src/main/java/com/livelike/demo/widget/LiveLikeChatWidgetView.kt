package com.livelike.demo.widget

import android.content.Context
import android.content.res.Resources
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.TypedValue
import android.view.Choreographer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
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
import com.livelike.engagementsdk.LiveLikeContentSession
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

    lateinit var contentSession: LiveLikeContentSession
    private var renderWidget = true
    lateinit var chatView: ChatView;
    private var chatViewBinding: FcChatViewBinding? = null
    var fallback: Choreographer.FrameCallback;
    var chatSession: LiveLikeChatSession? = null
    var chatRoomId = ""
    var userAvatarUrl = ""
    private var pinMessageAdapter = PinMessageAdapter(ArrayList())

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
        chatViewBinding!!.pinnedMessageList.adapter = pinMessageAdapter
        chatViewBinding?.let {
            chatView = it.chatView
            addView(it.root)
        }

    }

    override fun onHostResume() {
        contentSession.resume()
    }

    override fun onHostPause() {
        contentSession.pause()
    }

    override fun onHostDestroy() {
        contentSession.close()
        chatView.clearSession()
        chatSession?.close()
    }

    fun updateContentSession(contentSession: LiveLikeContentSession) {
        this.contentSession = contentSession;
    }

    fun updateChatSession(chatSession: LiveLikeChatSession?) {
        this.chatSession = chatSession
    }

    fun manuallyLayoutChildren() {
        for (i in 0 until getChildCount()) {
            var child = getChildAt(i);
            child.measure(
                MeasureSpec.makeMeasureSpec(getMeasuredWidth(), MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(getMeasuredHeight(), MeasureSpec.EXACTLY)
            );
            child.layout(0, 0, child.getMeasuredWidth(), child.getMeasuredHeight());
        }
    }


    fun configureChatView(chatSession: LiveLikeChatSession?, chatRoomId: String) {

        this.chatRoomId = chatRoomId
        connectToChatRoom(chatRoomId)
        setUserAvatar()
        registerMessageListener()
        registerVideoMessageHandler()

        if (chatSession != null) {
            chatView.allowMediaFromKeyboard = false
            chatView.isChatInputVisible = false
            chatView.setSession(chatSession)
        }
    }


    fun setAvatar(avatarUrl: String) {
        this.userAvatarUrl = avatarUrl
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

                }
            })
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

                chatViewThemeAttributes.apply {
                    (holder.itemView as FCVideoView)._binding?.let {

                        // Setting nickName
                        it.chatNickname.setTextColor(chatNickNameColor)
                        it.chatNickname.text = liveLikeChatMessage.nickname
                        it.chatNickname.setTextSize(
                            TypedValue.COMPLEX_UNIT_PX,
                            chatUserNameTextSize
                        )
                        it.chatNickname.isAllCaps = chatUserNameTextAllCaps


                        // Handle Chat background
                        val chatBackgroundLayoutParams =
                            it.chatBackground.layoutParams as ConstraintLayout.LayoutParams
                        chatBackgroundLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                        chatBackgroundLayoutParams.setMargins(
                            chatMarginLeft,
                            chatMarginTop + dpToPx(6),
                            chatMarginRight,
                            chatMarginBottom
                        )
                        it.chatBackground.layoutParams = chatBackgroundLayoutParams

                        // Handle Chat Bubble background
                        val chatBubbleLayoutParams: LinearLayout.LayoutParams =
                            it.chatBubbleBackground.layoutParams as LinearLayout.LayoutParams
                        it.chatBubbleBackground.setBackgroundResource(R.drawable.ic_chat_message_bubble_rounded_rectangle)
                        it.chatBubbleBackground.setPadding(
                            chatBubblePaddingLeft,
                            chatBubblePaddingTop,
                            chatBubblePaddingRight,
                            dpToPx(0)
                        )
                        chatBubbleLayoutParams.setMargins(
                            chatBubbleMarginLeft - dpToPx(12),
                            chatBubbleMarginTop,
                            chatBubbleMarginRight,
                            chatBubbleMarginBottom
                        )
                        chatBubbleLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                        it.chatBubbleBackground.layoutParams = chatBubbleLayoutParams


                        // Handle Avatar
                        it.imgChatAvatar.visibility = when (showChatAvatar) {
                            true -> View.VISIBLE
                            else -> View.GONE
                        }
                        val avatarLayoutParams = LinearLayout.LayoutParams(
                            chatAvatarWidth,
                            chatAvatarHeight
                        )
                        avatarLayoutParams.setMargins(
                            chatAvatarMarginLeft,
                            chatAvatarMarginTop,
                            chatAvatarMarginRight,
                            chatAvatarMarginBottom
                        )
                        avatarLayoutParams.gravity = chatAvatarGravity
                        it.imgChatAvatar.layoutParams = avatarLayoutParams
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
                        if (liveLikeChatMessage.custom_data.isNullOrEmpty()) {
                            // load local image
                            Glide.with(holder.itemView.context.applicationContext)
                                .load(R.drawable.default_avatar)
                                //.apply(options)
                                .placeholder(chatUserPicDrawable)
                                .into(it.imgChatAvatar)
                        } else {

                            val jsonObject = JSONObject(liveLikeChatMessage.custom_data)
                            val userPic = jsonObject.get("userPic").toString()

                            Glide.with(holder.itemView.context.applicationContext)
                                .load(userPic)
                                //.apply(options)
                                .placeholder(chatUserPicDrawable)
                                .error(chatUserPicDrawable)
                                .into(it.imgChatAvatar)
                        }

                        // Handle VideoView - AN entry to the flow of the Video View.
                        val jsonObject = JSONObject(liveLikeChatMessage.custom_data)
                        val url = jsonObject.get("url").toString()
                        url?.let {
                            val videoViewHolder = (holder as FCVideoViewHolder)
                            val fcVideoView = videoViewHolder.itemView as FCVideoView


                            try {
                                val videoThumbnail = jsonObject.get("videoThumbnail").toString()
                                videoThumbnail.let {
                                    fcVideoView.setVideoThumbnail(it)
                                }
                            } catch (e: Exception) {
                            }
                            videoViewHolder.videoUrl = it
                        }

                    }
                }
            }
        }

    }

    class FCVideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var videoUrl: String? = null
            set(value) {
                field = value
                (itemView as FCVideoView).videoUrl = value
            }
    }


    private fun registerMessageListener() {

        chatSession?.setMessageListener(object : MessageListener {
            override fun onDeleteMessage(messageId: String) {
                Log.i("Delete Message", messageId)
            }

            override fun onHistoryMessage(messages: List<LiveLikeChatMessage>) {
                Log.i("History Message", messages.toString())
            }

            override fun onNewMessage(message: LiveLikeChatMessage) {
                val imm =
                    context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm?.hideSoftInputFromWindow(
                    chatView.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS
                )
            }

            override fun onPinMessage(message: PinMessageInfo) {
                Handler(Looper.getMainLooper()).post(Runnable {
                    pinMessageAdapter.addPinMessage(message)
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
        pinMessageAdapter.addPinMessages(pinnedMessages as ArrayList<PinMessageInfo>)
    }

    fun sendEvent(
        eventName: String,
        params: WritableMap?
    ) {
        val reactContext = this.getContext() as ReactContext;
        reactContext.getJSModule(RCTEventEmitter::class.java)
            .receiveEvent(this.getId(), eventName, params)
    }


    companion object {
        const val CHAT_MESSAGE_SENT = "onChatMessageSent"
    }
}
