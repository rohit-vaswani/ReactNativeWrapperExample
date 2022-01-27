package com.livelike.demo.widget

import android.util.Log
import android.view.Choreographer
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.facebook.react.bridge.LifecycleEventListener
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.uimanager.ThemedReactContext
import com.livelike.demo.R
import com.livelike.demo.ui.main.FCChatView


class LiveLikeWidgetView(
    val context: ThemedReactContext,
    private val applicationContext: ReactApplicationContext
) : ConstraintLayout(context) { // TODO: LifecycleEventListener


    lateinit var chatView: FCChatView
    var fallback: Choreographer.FrameCallback;
    var renderWidget = true // TODO: Added the hard-coding

    init {
        Log.i("Reached", "LiveLikeWidget")
//        this.applicationContext.addLifecycleEventListener(this)
        this.fallback = Choreographer.FrameCallback() {
            manuallyLayoutChildren();
            viewTreeObserver.dispatchOnGlobalLayout();
            if (renderWidget) {
                Choreographer.getInstance().postFrameCallback(this!!.fallback)
            }
        }
        Choreographer.getInstance().postFrameCallback(this.fallback)

        // TODO: Need to check this
//        val parentView = LayoutInflater.from(context).inflate(R.layout.fc_widget_view, null) as LinearLayout;
//        chatView = parentView.findViewById(R.id.widget_view);
//        addView(parentView)

        val view = LayoutInflater.from(context).inflate(R.layout.fc_chat_view, null)
        this.addView(view)
        chatView = view as FCChatView
    }


    fun setter() {

    }

    fun connectToChatRoom(){

    }

//    override fun onHostDestroy() {
//        TODO("Not yet implemented")
//    }
//
//    override fun onHostPause() {
//        TODO("Not yet implemented")
//    }
//
//    override fun onHostResume() {
//        TODO("Not yet implemented")
//    }



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
}