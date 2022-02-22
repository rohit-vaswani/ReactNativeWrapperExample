package com.livelike.demo.widget

import android.util.Log
import android.widget.Toast
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.common.MapBuilder
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.ViewGroupManager
import com.facebook.react.uimanager.annotations.ReactProp
import com.livelike.demo.LiveLikeManager
import com.livelike.engagementsdk.EngagementSDK
import com.livelike.engagementsdk.EpochTime
import com.livelike.engagementsdk.chat.LiveLikeChatSession
import com.livelike.engagementsdk.chat.data.remote.LiveLikeOrdering
import com.livelike.engagementsdk.chat.data.remote.LiveLikePagination
import com.livelike.engagementsdk.chat.data.remote.PinMessageInfo
import com.livelike.engagementsdk.publicapis.ErrorDelegate
import com.livelike.engagementsdk.publicapis.LiveLikeCallback
import java.util.*

class LiveLikeChatViewManager(val applicationContext: ReactApplicationContext) :
    ViewGroupManager<LiveLikeChatWidgetView>() {

    val REACT_CLASS = "LiveLikeChatWidgetView"
    var chatSession: LiveLikeChatSession? = null
    var chatRoomId = ""



    companion object {
        const val EVENT_WIDGET_SHOWN = "widgetShown"
        const val EVENT_WIDGET_HIDDEN = "widgetHidden"
        const val EVENT_ANALYTICS = "analytics"
        const val COMMAND_SEND_MESSAGE = 0
        const val COMMAND_UPDATE_NICK_NAME = 1
        const val COMMAND_UPDATE_USER_AVATAR = 2
    }

    override fun getName(): String {
        return REACT_CLASS
    }


    override fun getCommandsMap(): Map<String, Int>? {
        val hashMap = hashMapOf<String, Int>()
        hashMap.put("sendMessage", COMMAND_SEND_MESSAGE)
        hashMap.put("updateNickName", COMMAND_UPDATE_NICK_NAME)
        hashMap.put("updateUserAvatar", COMMAND_UPDATE_USER_AVATAR)
        return hashMap
    }

    override fun receiveCommand(
        root: LiveLikeChatWidgetView,
        commandId: String,
        args: ReadableArray?
    ) {
        super.receiveCommand(root, commandId, args)
        val commandIdInt = commandId.toInt()
        when (commandIdInt) {
            COMMAND_SEND_MESSAGE -> sendMessage(root, args)
            COMMAND_UPDATE_NICK_NAME -> updateNickName(root, args)
            COMMAND_UPDATE_USER_AVATAR -> updateUserAvatar(root, args)
            else -> { }
        }
    }

    private fun updateNickName(
        view: LiveLikeChatWidgetView,
        args: ReadableArray?
    ) {
        val nickName = args?.getString(1)
        setNickName(nickName)
    }

    private fun updateUserAvatar(
        view: LiveLikeChatWidgetView,
        args: ReadableArray?
    ) {
        val userAvatar = args?.getString(1)
        userAvatar?.let {
            view.setAvatar(it)
        }
    }



    private fun scrollToBottom(
        view: LiveLikeChatWidgetView,
    ) {
        view.scrollToBottom()
    }

    private fun sendMessage(
        view: LiveLikeChatWidgetView,
        args: ReadableArray?
    ) {
        val message = args?.getString(1)
        message?.let {
            view.sendChatMessage(it)
            view.scrollToBottom()
        }
    }

    override fun createViewInstance(reactContext: ThemedReactContext): LiveLikeChatWidgetView {
        return LiveLikeChatWidgetView(reactContext, applicationContext);
    }

    @ReactProp(name = "programId")
    fun setProgramId(view: LiveLikeChatWidgetView, programId: String) {

        // content session
        val contentSession = LiveLikeManager.engagementSDK.createContentSession(programId)
        view.updateContentSession(contentSession)

        // chat session
        chatSession = createChatSession()
        view.updateChatSession(chatSession)

        onConfiguration(view)
    }

    @ReactProp(name = "chatRoomId")
    fun setChatRoomId(view: LiveLikeChatWidgetView, chatRoomId: String) {
        this.chatRoomId = chatRoomId
        onConfiguration(view)
    }


    @ReactProp(name = "userAvatarUrl")
    fun setUserAvatar(view: LiveLikeChatWidgetView, avatarUrl: String) {
        view.setAvatar(avatarUrl)
    }

    @ReactProp(name = "userNickName")
    fun setUserNickName(view: LiveLikeChatWidgetView, nickName: String?) {
        setNickName(nickName)
    }


    private fun onConfiguration(chatView: LiveLikeChatWidgetView) {
        if (isChatConfigurable()) {
            chatView.configureChatView(chatSession, this.chatRoomId)
            this.registerPinnedMessageHandler(chatView)
        }
    }

    override fun onDropViewInstance(view: LiveLikeChatWidgetView) {
        super.onDropViewInstance(view)
    }

    override fun getExportedCustomDirectEventTypeConstants(): Map<String, Any> {
        var map = HashMap<String, Any>()
        map.put(EVENT_WIDGET_SHOWN, MapBuilder.of("registrationName", "onWidgetShown"));
        map.put(EVENT_WIDGET_HIDDEN, MapBuilder.of("registrationName", "onWidgetHidden"));
        map.put(EVENT_ANALYTICS, MapBuilder.of("registrationName", "onEvent"));
        map.put(LiveLikeChatWidgetView.CHAT_MESSAGE_SENT, MapBuilder.of("registrationName", LiveLikeChatWidgetView.CHAT_MESSAGE_SENT));
        map.put(LiveLikeChatWidgetView.EVENT_VIDEO_PLAYED, MapBuilder.of("registrationName", LiveLikeChatWidgetView.EVENT_VIDEO_PLAYED));
        map.put(LiveLikeChatWidgetView.EVENT_ASK_INFLUENCER, MapBuilder.of("registrationName", LiveLikeChatWidgetView.EVENT_ASK_INFLUENCER));
        return map;
    }


    // Helper Functions
    private fun setNickName(nickName: String?) {

        if (nickName == "" || nickName == null) {
            return
        }

        LiveLikeManager.engagementSDK.updateChatNickname(nickName)
    }


    private fun createChatSession(): LiveLikeChatSession? {

        return LiveLikeManager.engagementSDK.createChatSession(object :
            EngagementSDK.TimecodeGetter {
            override fun getTimecode(): EpochTime {
                return EpochTime(0)
            }

        }, errorDelegate = object : ErrorDelegate() {
            override fun onError(error: String) {

            }
        })
    }


    private fun registerPinnedMessageHandler(chatView: LiveLikeChatWidgetView){


        LiveLikeManager.engagementSDK.chat()?.getPinMessageInfoList(
            chatRoomId!!,
            LiveLikeOrdering.ASC,
            LiveLikePagination.FIRST,
            object : LiveLikeCallback<List<PinMessageInfo>>() {
                override fun onResponse(result: List<PinMessageInfo>?, error:
                String?) {
                    result?.let {
                        chatView.handleHistoricalPinMessages(result)
                    }
                }
            })

    }


    private fun isChatConfigurable(): Boolean {
        return this.chatRoomId != "" && this.chatSession != null
    }

}
