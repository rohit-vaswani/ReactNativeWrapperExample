package com.livelike.demo.widget

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.common.MapBuilder
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.ViewGroupManager
import com.facebook.react.uimanager.annotations.ReactProp
import com.livelike.demo.LiveLikeManager
import java.util.*


class LiveLikeWidgetViewManager(val applicationContext: ReactApplicationContext) : ViewGroupManager<LiveLikeWidgetView>() {

    val REACT_CLASS = "LiveLikeWidgetView"

    companion object {
        const val EVENT_WIDGET_SHOWN = "widgetShown"
        const val EVENT_WIDGET_HIDDEN = "widgetHidden"
        const val EVENT_ANALYTICS = "analytics"
    }

    override fun getName(): String {
        return REACT_CLASS
    }

    override fun createViewInstance(reactContext: ThemedReactContext): LiveLikeWidgetView {
        return LiveLikeWidgetView(reactContext, applicationContext);
    }


    @ReactProp(name = "programId")
    fun setProgramId(view: LiveLikeWidgetView, programId: String) {
        val session = LiveLikeManager.engagementSDK.createContentSession(programId)
        view.updateContentSession(session)
    }

    override fun onDropViewInstance(view: LiveLikeWidgetView) {
        super.onDropViewInstance(view)
    }

    override fun getExportedCustomDirectEventTypeConstants(): Map<String, Any> {
        var map = HashMap<String, Any>()
        map.put(EVENT_WIDGET_SHOWN, MapBuilder.of("registrationName", "onWidgetShown"));
        map.put(EVENT_WIDGET_HIDDEN, MapBuilder.of("registrationName", "onWidgetHidden"));
        map.put(EVENT_ANALYTICS, MapBuilder.of("registrationName", "onEvent"));
        return map;
    }
}