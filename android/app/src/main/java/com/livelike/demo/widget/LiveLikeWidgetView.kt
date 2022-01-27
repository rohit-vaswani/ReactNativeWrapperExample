package com.livelike.demo.widget

import android.util.Log
import android.view.Choreographer
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.appcompat.widget.ViewUtils
import com.facebook.react.bridge.*
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.events.RCTEventEmitter
import com.livelike.demo.LiveLikeManager
import com.livelike.demo.R
import com.livelike.engagementsdk.LiveLikeContentSession
import com.livelike.engagementsdk.LiveLikeWidget
import com.livelike.engagementsdk.WidgetListener
import com.livelike.engagementsdk.core.services.messaging.proxies.LiveLikeWidgetEntity
import com.livelike.engagementsdk.core.services.messaging.proxies.WidgetInterceptor
import com.livelike.engagementsdk.widget.view.WidgetView

class LiveLikeWidgetView(val context: ThemedReactContext, val applicationContext: ReactApplicationContext) : LinearLayout(context),
    LifecycleEventListener {

    lateinit var contentSession: LiveLikeContentSession
    var widgetView: WidgetView;
    var fallback: Choreographer.FrameCallback;
    private var renderWidget = false

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
        val parentView = LayoutInflater.from(context).inflate(R.layout.fc_widget_view, null) as LinearLayout;
        widgetView = parentView.findViewById(R.id.widget_view);
        addView(parentView)
    }

    override fun onHostResume() {
        contentSession.resume()
    }

    override fun onHostPause() {
        contentSession.pause()
    }

    override fun onHostDestroy() {
        contentSession.close()
    }

    fun updateContentSession(contentSession: LiveLikeContentSession) {
        this.contentSession = contentSession;

        contentSession.widgetInterceptor = object : WidgetInterceptor() {


            override fun widgetWantsToShow(widgetData: LiveLikeWidgetEntity) {
                showWidget()
                renderWidget = true
                Choreographer.getInstance().postFrameCallback(fallback)
            }

        }

//        widgetView!!.setSession(contentSession, object : WidgetListener {


//            override fun onRemoveWidget() {
//                renderWidget = false
//                val params = Arguments.createMap()
//                sendEvent(LiveLikeWidgetViewManager.EVENT_WIDGET_HIDDEN, params)
//            }
//
//            override fun onNewWidget(liveLikeWidget: LiveLikeWidget) {
//                val params = Arguments.createMap()
//                params.putInt("height", ViewUtils.pxToDp(context, height))
//                params.putInt("width", ViewUtils.pxToDp(context, width))
//                sendEvent(LiveLikeWidgetViewManager.EVENT_WIDGET_SHOWN, params)
//            }
//        });
//        contentSession.analyticService.setEventObserver { eventKey, eventJson ->
//            run {
//                val params = Arguments.createMap()
//                params.putString("eventKey", eventKey)
//                params.putString("eventJson", eventJson.toString())
//                sendEvent(LiveLikeWidgetViewManager.EVENT_ANALYTICS, params)
//            }
//        }
    }

    fun manuallyLayoutChildren() {
        for (i in 0 until getChildCount()) {
            var child = getChildAt(i);
            child.measure(
                MeasureSpec.makeMeasureSpec(getMeasuredWidth(), MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(getMeasuredHeight(), MeasureSpec.EXACTLY));
            child.layout(0, 0, child.getMeasuredWidth(), child.getMeasuredHeight());
        }
    }

    fun sendEvent(
        eventName: String,
        params: WritableMap?) {
        val reactContext = this.getContext() as ReactContext;
        reactContext.getJSModule(RCTEventEmitter::class.java).receiveEvent(this.getId(), eventName, params)
    }
}