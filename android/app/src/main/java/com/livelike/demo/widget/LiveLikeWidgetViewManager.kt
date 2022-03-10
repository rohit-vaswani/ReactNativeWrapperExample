package com.livelike.demo.widget

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.common.MapBuilder
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.ViewGroupManager
import com.facebook.react.uimanager.annotations.ReactProp
import com.livelike.demo.LiveLikeManager
import java.util.*


class LiveLikeWidgetViewManager(val applicationContext: ReactApplicationContext) :
    ViewGroupManager<LiveLikeWidgetView>() {

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
        val session = LiveLikeManager.getContentSession(programId)
        session?.let {
            view.updateContentSession(it)
        }

    }

    @ReactProp(name = "influencerName")
    fun setInfluencerName(view: LiveLikeWidgetView, influencerName: String) {
        view.setInfluencerName(influencerName)
    }

    @ReactProp(name = "showAskWidget")
    fun showAskWidget(view: LiveLikeWidgetView, showWidget: Boolean) {
        if (showWidget) {
            view.displayAskWidget()
        }
    }

    override fun onDropViewInstance(view: LiveLikeWidgetView) {
        super.onDropViewInstance(view)
    }

    override fun getExportedCustomDirectEventTypeConstants(): Map<String, Any> {
        var map = HashMap<String, Any>()
        map.put(EVENT_WIDGET_SHOWN, MapBuilder.of("registrationName", "onWidgetShown"));
        map.put(EVENT_WIDGET_HIDDEN, MapBuilder.of("registrationName", "onWidgetHidden"));
        map.put(LiveLikeWidgetView.EVENT_INFLUENCER_MESSAGE_SENT, MapBuilder.of("registrationName", LiveLikeWidgetView.EVENT_INFLUENCER_MESSAGE_SENT));
        map.put(EVENT_ANALYTICS, MapBuilder.of("registrationName", "onEvent"));
        return map;
    }
}