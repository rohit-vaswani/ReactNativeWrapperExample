package com.livelike.demo

import android.app.Application
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.livelike.engagementsdk.chat.ChatRoomInfo
import com.livelike.engagementsdk.publicapis.LiveLikeCallback

class RNLiveLikeModule(
    val application: Application,
    private val applicationContext: ReactApplicationContext
) : ReactContextBaseJavaModule(applicationContext) {

    override fun getName(): String {
        return "LiveLikeModule"
    }

    @ReactMethod
    fun initializeSDK(clientId: String) {
        LiveLikeManager.initializeSDK(application, clientId)
    }

    @ReactMethod
    fun getChatRoomName(chatRoomId: String, promise: Promise) {
        LiveLikeManager.engagementSDK.chat().getChatRoom(chatRoomId, object : LiveLikeCallback<ChatRoomInfo>() {
                override fun onResponse(chatRoomDetails: ChatRoomInfo?, error: String?) {
                    chatRoomDetails?.let {
                        return promise.resolve(chatRoomDetails?.title)
                    }
                    error?.let {
                        return promise.reject(error)
                    }
                }
            })
    }
}