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
import com.livelike.engagementsdk.publicapis.ErrorDelegate
import com.livelike.engagementsdk.publicapis.LiveLikeUserApi

object LiveLikeManager {


    lateinit var engagementSDK: EngagementSDK;
    var userAccessToken: String? = null;
    var contentSession: LiveLikeContentSession? = null


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


    private fun createContentSession(programId: String): LiveLikeContentSession {
        return engagementSDK.createContentSession(programId)
    }

    fun getChatSession(programId: String): LiveLikeChatSession? {

        if(isValidContentSession(programId)) {
            return this.contentSession?.chatSession
        }

        destroyContentSession()
        return createContentSession(programId)?.chatSession

    }

    fun getContentSession(programId: String): LiveLikeContentSession? {

        if(isValidContentSession(programId)) {
            return this.contentSession
        }

        destroyContentSession()
        return createContentSession(programId)

    }

    @ReactMethod
    fun destroyContentSession() {
        this.contentSession?.close()
        this.contentSession = null
    }

    private fun isValidContentSession(programId: String): Boolean {
        return this.contentSession?.contentSessionId() == programId
    }


    @ReactMethod
    fun initializeSDK(applicationContext: Context, clientId: String) {


        // TODO: See if this is already present
//        val sharedPreferences = applicationContext.getSharedPreferences(
//            MainActivity.ID_SHARED_PREFS,
//            Context.MODE_PRIVATE
//        )

        engagementSDK = clientId?.let {
            EngagementSDK(
                it,
                applicationContext,
                object : ErrorDelegate() {
                    override fun onError(error: String) {
                        println("LiveLikeApplication.onError--->$error")
                    }
                }, accessTokenDelegate = object : AccessTokenDelegate {
                    override fun getAccessToken(): String? {
                        return applicationContext.getSharedPreferences(
                            applicationContext.packageName,
                            Context.MODE_PRIVATE
                        ).getString(
                            PREF_USER_ACCESS_TOKEN,
                            null
                        ).apply {
                            println("Token:$this")
                        }
                    }

                    override fun storeAccessToken(accessToken: String?) {

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