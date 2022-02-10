package com.livelike.demo

import android.app.Application
import com.facebook.react.ReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.uimanager.ViewManager
import com.livelike.demo.widget.LiveLikeChatViewManager
import com.livelike.demo.widget.LiveLikeWidgetViewManager
import java.util.*

// replace with your package

class RNLiveLikePackage(val application: Application) : ReactPackage {
    override fun createNativeModules(reactContext: ReactApplicationContext): MutableList<NativeModule> {
        return Arrays.asList<NativeModule>(RNLiveLikeModule(application, reactContext))
    }

    override fun createViewManagers(reactContext: ReactApplicationContext): List<ViewManager<*, *>> {
        return Arrays.asList<ViewManager<*, *>>(LiveLikeChatViewManager(reactContext), LiveLikeWidgetViewManager(reactContext))
    }
}