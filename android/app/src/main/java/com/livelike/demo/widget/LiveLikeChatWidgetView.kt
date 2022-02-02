package com.livelike.demo.widget

import android.util.Log
import android.view.Choreographer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.facebook.react.bridge.LifecycleEventListener
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.uimanager.ThemedReactContext
import com.livelike.demo.R
import com.livelike.demo.databinding.FcChatViewBinding
import com.livelike.demo.ui.main.VideoView
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
import okhttp3.internal.notifyAll
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
                    if (error != null) {
                        Log.e("TEST", error)
                    }
                }
            })
    }


    private fun registerVideoMessageHandler() {

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
                // Do not do anything
                chatViewThemeAttributes.chatBubbleBackgroundRes?.let {
                    val jsonObject = JSONObject(liveLikeChatMessage.custom_data)
                    val url = jsonObject.get("custom_message").toString()
                    (holder as MyCustomMsgViewHolder).videoUrl = url
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
                TODO("Not yet implemented")
            }

            override fun onUnPinMessage(pinMessageId: String) {
                TODO("Not yet implemented")
            }
        })

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