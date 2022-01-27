package com.livelike.demo

import android.app.Application
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod

class RNLiveLikeModule(val application: Application, private val applicationContext: ReactApplicationContext) : ReactContextBaseJavaModule(applicationContext) {

    override fun getName(): String {
        return "LiveLikeModule"
    }

    @ReactMethod
    fun initializeSDK(clientId: String) {
        LiveLikeManager.initializeSDK(application, clientId)
    }
}