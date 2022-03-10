package com.livelike.demo

import android.content.Context
import android.util.Log
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactMethod
import com.livelike.demo.ui.main.PageViewModel.Companion.PREF_USER_ACCESS_TOKEN
import com.livelike.engagementsdk.EngagementSDK
import com.livelike.engagementsdk.LiveLikeContentSession
import com.livelike.engagementsdk.chat.LiveLikeChatSession
import com.livelike.engagementsdk.core.AccessTokenDelegate
import com.livelike.engagementsdk.core.utils.LogLevel
import com.livelike.engagementsdk.core.utils.minimumLogLevel
import com.livelike.engagementsdk.publicapis.ErrorDelegate

object LiveLikeManager {


    lateinit var engagementSDK: EngagementSDK;
    var userAccessToken: String? = null;
    var contentSession: LiveLikeContentSession? = null
    const val PREF_USER_ACCESS_TOKEN = "user_access_token"


    @ReactMethod
    fun subscribeUserStream(key: String, promise: Promise) {
        engagementSDK.userStream.subscribe(key) {
            promise.resolve(it?.nickname)
            unSubscribeUserStream()
        }
    }

    @ReactMethod
    fun unSubscribeUserStream() {
        engagementSDK.userStream.subscribe("invalid-key") {}
    }


    @Synchronized private fun createContentSession(programId: String): LiveLikeContentSession {
        this.contentSession = engagementSDK.createContentSession(programId,  null, false)
        return this.contentSession!!

    }

    @Synchronized fun getChatSession(programId: String): LiveLikeChatSession? {
        if(isValidContentSession(programId)) {
            return this.contentSession?.chatSession
        }

        destroyContentSession()
        return createContentSession(programId)?.chatSession

    }

    @Synchronized fun getContentSession(programId: String): LiveLikeContentSession? {

        if(isValidContentSession(programId)) {
            return this.contentSession
        }

        destroyContentSession()
        return createContentSession(programId)

    }

    @ReactMethod
    @Synchronized fun destroyContentSession() {
        this.contentSession?.chatSession?.close()
        this.contentSession?.close()
        this.contentSession = null
    }

    @Synchronized private fun isValidContentSession(programId: String): Boolean {
        return this.contentSession?.contentSessionId() == programId
    }


    @ReactMethod
    fun initializeSDK(applicationContext: Context, clientId: String,  promise: Promise) {

        minimumLogLevel = LogLevel.Verbose

        engagementSDK = clientId?.let {
            EngagementSDK(
                it,
                applicationContext,
                object : ErrorDelegate() {
                    override fun onError(error: String) {
                        promise.reject(Throwable(error))
                    }
                }, accessTokenDelegate = object : AccessTokenDelegate {
                    override fun getAccessToken(): String? {
                        return applicationContext.getSharedPreferences(
                            applicationContext.packageName,
                            Context.MODE_PRIVATE
                        ).getString(
                            PREF_USER_ACCESS_TOKEN,
                            null
                        ).apply {}
                    }

                    override fun storeAccessToken(accessToken: String?) {

                        promise.resolve(accessToken)
                        userAccessToken = accessToken

                        applicationContext.getSharedPreferences(
                            applicationContext.packageName,
                            Context.MODE_PRIVATE
                        ).edit().putString(
                            PREF_USER_ACCESS_TOKEN,
                            accessToken
                        ).apply()
                    }
                })
        }!!
    }
}