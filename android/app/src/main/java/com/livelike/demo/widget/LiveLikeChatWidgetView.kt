package com.livelike.demo.widget

import android.content.res.Resources
import android.util.Log
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
import com.facebook.react.bridge.LifecycleEventListener
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.uimanager.ThemedReactContext
import com.livelike.demo.LiveLikeManager
import com.livelike.demo.R
import com.livelike.demo.adapters.PinMessageAdapter
import com.livelike.demo.databinding.FcChatViewBinding
import com.livelike.demo.ui.main.VideoView
import com.livelike.engagementsdk.LiveLikeContentSession
import com.livelike.engagementsdk.MessageListener
import com.livelike.engagementsdk.chat.*
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
        registerInputListener()

        Log.i("Room name", chatSession?.getCurrentChatRoom.toString())

        if (chatSession != null) {
            chatView.allowMediaFromKeyboard = true
            chatView.isChatInputVisible = true
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


        var chatAvatarUrl = this.userAvatarUrl

        chatView.chatViewDelegate = object : ChatViewDelegate {
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: ChatMessageType
            ): RecyclerView.ViewHolder {
                // TODO Should we return the default view holder for normal message
                return MyCustomMsgViewHolder(VideoView(parent.context))
            }

            override fun onBindViewHolder(
                holder: RecyclerView.ViewHolder,
                liveLikeChatMessage: LiveLikeChatMessage,
                chatViewThemeAttributes: ChatViewThemeAttributes,
                showChatAvatar: Boolean
            ) {

                chatViewThemeAttributes.apply {
                    (holder.itemView as VideoView)._binding?.let {

                        // Setting nickName
                        // TODO: Handle this as the current user
                        it.chatNickname.setTextColor(chatNickNameColor)
                        it.chatNickname.text = liveLikeChatMessage.nickname
                        it.chatNickname.setTextSize(
                            TypedValue.COMPLEX_UNIT_PX,
                            chatUserNameTextSize
                        )
                        it.chatNickname.isAllCaps = chatUserNameTextAllCaps


                        // Handle Chat background
                        val layoutParam =
                            it.chatBackground.layoutParams as ConstraintLayout.LayoutParams
                        it.chatBubbleBackground.setBackgroundResource(R.drawable.ic_chat_message_bubble_rounded_rectangle)
                        layoutParam.setMargins(
                            chatMarginLeft,
                            chatMarginTop + dpToPx(6),
                            chatMarginRight,
                            chatMarginBottom
                        )
                        layoutParam.width = ViewGroup.LayoutParams.MATCH_PARENT
                        it.chatBackground.layoutParams = layoutParam

                        // Handle Chat Bubble background
                        it.chatBubbleBackground.setPadding(
                            chatBubblePaddingLeft,
                            chatBubblePaddingTop,
                            chatBubblePaddingRight,
                            chatBubblePaddingBottom
                        )
                        val layoutParam1: LinearLayout.LayoutParams =
                            it.chatBubbleBackground.layoutParams as LinearLayout.LayoutParams
                        layoutParam1.setMargins(
                            chatBubbleMarginLeft,
                            chatBubbleMarginTop,
                            chatBubbleMarginRight,
                            chatBubbleMarginBottom
                        )
                        layoutParam1.width = ViewGroup.LayoutParams.MATCH_PARENT
                        it.chatBubbleBackground.layoutParams = layoutParam1


                        // Handle Avatar
                        it.imgChatAvatar.visibility =
                            when (showChatAvatar) {
                                true -> View.VISIBLE
                                else -> View.GONE
                            }
                        val layoutParamAvatar = LinearLayout.LayoutParams(
                            chatAvatarWidth,
                            chatAvatarHeight
                        )
                        layoutParamAvatar.setMargins(
                            chatAvatarMarginLeft,
                            chatAvatarMarginTop,
                            chatAvatarMarginRight,
                            chatAvatarMarginBottom
                        )
                        layoutParamAvatar.gravity = chatAvatarGravity
                        it.imgChatAvatar.layoutParams = layoutParamAvatar
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
                        if (chatAvatarUrl.isNullOrEmpty()) { // liveLikeChatMessage.userPic
                            // load local image
                            Glide.with(holder.itemView.context.applicationContext)
                                .load(R.drawable.default_avatar)
                                //.apply(options)
                                .placeholder(chatUserPicDrawable)
                                .into(it.imgChatAvatar)
                        } else {
                            Glide.with(holder.itemView.context.applicationContext)
                                .load(chatAvatarUrl)
                                //.apply(options)
                                .placeholder(chatUserPicDrawable)
                                .error(chatUserPicDrawable)
                                .into(it.imgChatAvatar)
                        }

                        // Handle VideoView
                        val jsonObject = JSONObject(liveLikeChatMessage.custom_data)
                        val url = jsonObject.get("custom_message").toString()
                        (holder as MyCustomMsgViewHolder).videoUrl = url
                    }
                }
            }
        }

    }

    // TOOD: Change the name
    class MyCustomMsgViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var videoUrl: String? = null
            set(value) {
                field = value
                (itemView as VideoView).videoUrl = value
            }
    }


//    Test Area

    private fun registerInputListener() {


        val url = "http://techslides.com/demos/sample-videos/small.mp4"

        chatViewBinding?.customChatMessageSendBtn?.setOnClickListener {
//            val url = chatViewBinding?.urlInput?.text
            url?.let {
                chatSession?.sendCustomChatMessage("{" +
                        "\"custom_message\": \"" + url + "\"" +
                        "}", object : LiveLikeCallback<LiveLikeChatMessage>() {
                    override fun onResponse(result: LiveLikeChatMessage?, error: String?) {
                        result?.let {
                            println("ExoPlayerActivity.onResponse> ${it.id}")
                        }
                        error?.let {
                            //Toast.makeText(applicationContext, error, Toast.LENGTH_SHORT).show()
                        }
                    }
                })
            }
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
                Log.i("New Message", message.toString())
            }

            override fun onPinMessage(message: PinMessageInfo) {
                pinMessageAdapter.addMessageToList(message)
            }

            override fun onUnPinMessage(pinMessageId: String) {
                pinMessageAdapter.removeMessageFromList(pinMessageId)
            }
        })

    }


    fun dpToPx(dp: Int): Int {
        val scale = Resources.getSystem().displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }


    fun handleHistoricalPinMessages(pinnedMessages: List<PinMessageInfo>) {
        pinMessageAdapter.addMessages(pinnedMessages as ArrayList<PinMessageInfo>)
    }
}

/*


    <com.livelike.engagementsdk.chat.ChatView xmlns:android="http://schemas.android.com/apk/res/android"
        xmlns:app="http://schemas.android.com/apk/res-auto"
        android:id="@+id/chat_view"
        android:layout_width="100dp"
        android:layout_height="100dp"
        android:background="@color/red"
        app:displayUserProfile="true"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"

        />


        //        val parentView = LayoutInflater.from(context).inflate(R.layout.fc_chat_view, null) as ConstraintLayout;
//        chatView = parentView.findViewById(R.id.chat_view);
//        addView(parentView)

 */