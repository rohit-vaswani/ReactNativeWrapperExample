package com.livelike.demo
import android.util.Log
import android.view.Choreographer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.ViewGroupManager
import com.facebook.react.uimanager.annotations.ReactProp
import com.facebook.react.uimanager.annotations.ReactPropGroup
import com.livelike.demo.ui.main.FCChatView
import com.livelike.demo.widget.LiveLikeWidgetView


/*
    replace with your package
    Write a Wrapper for ChatView and convert Fragment Logic/lifecycle into View lifecycle.
    Along with View, expose a Module whose singleton instance will be created on RN SIDE AND initialized.
 */


class LiveLikeWidgetViewManager(val applicationContext: ReactApplicationContext) : ViewGroupManager<LiveLikeWidgetView>() {


    val REACT_CLASS = "LiveLikeWidgetView"
    private var propWidth = 0
    private var propHeight = 0

    companion object {
        const val COMMAND_CREATE = 0
    }


    override fun getName(): String {
        return REACT_CLASS
    }

    public override fun createViewInstance(reactContext: ThemedReactContext): LiveLikeWidgetView {
        return LiveLikeWidgetView(reactContext, applicationContext)
    }

    override fun getCommandsMap(): Map<String, Int>? {
        val hashMap = hashMapOf<String, Int>()
        hashMap.apply {
            put("create", COMMAND_CREATE)
        }
        return hashMap
    }

    override fun receiveCommand(
        root: LiveLikeWidgetView,
        commandId: String,
        args: ReadableArray?
    ) {
        super.receiveCommand(root, commandId, args)
        val reactNativeViewId = args!!.getInt(0)
        when (commandId.toInt()) {
            COMMAND_CREATE -> addChatView(root, reactNativeViewId)
            else -> {}
        }
    }

    @ReactPropGroup(names = ["width", "height"], customType = "Style")
    fun setStyle(view: LiveLikeWidgetView?, index: Int, value: Int) {
        if (index == 0) {
            propWidth = value
        }
        if (index == 1) {
            propHeight = value
        }
    }

    @ReactProp(name = "programId")
    fun setProgramId(view: LiveLikeWidgetView?, programId: String) {
        Log.i("programId", programId)
    }

    @ReactProp(name = "chatRoomId")
    fun setChatRoomId(view: LiveLikeWidgetView?, chatRoomId: String) {
        Log.i("chatRoomId", chatRoomId)
    }

    @ReactProp(name = "clientId")
    fun setClientId(view: LiveLikeWidgetView?, clientId: String) {
        Log.i("clientId", clientId)
    }

    private fun addChatView(root: LiveLikeWidgetView, reactNativeViewId: Int) {
//        val parentView = root.findViewById<View>(reactNativeViewId) as ViewGroup
//        addChatFrame(parentView)
    }


//    private fun addChatFrame(parentView: ViewGroup) {
//        Choreographer.getInstance().postFrameCallback(object : Choreographer.FrameCallback {
//            override fun doFrame(frameTimeNanos: Long) {
//                manuallyLayoutChildren(parentView)
//                parentView.viewTreeObserver.dispatchOnGlobalLayout()
//                Choreographer.getInstance().postFrameCallback(this)
//                val view: FCChatView = LayoutInflater.from(reactContext.currentActivity).inflate(R.layout.fc_chat_view, parentView, false) as FCChatView
//                parentView.addView(view)
//                view.initialize(clientId, programId, chatRoomId)
//            }
//        })
//    }

//    fun manuallyLayoutChildren(view: View) {
//        // propWidth and propHeight coming from react-native props
//        val width = propWidth
//        val height = propHeight
//        view.measure(
//            View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
//            View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)
//        )
//        view.layout(0, 0, width, height)
//    }
}


/*


        val activity = reactContext.currentActivity as FragmentActivity?
        chatFragment = ChatView.newInstance(programId, chatRoomId, isChatVisible)
         val myFragment = WidgetTimeLineFragment()
        activity!!.supportFragmentManager
            .beginTransaction()
            .replace(reactNativeViewId, chatFragment, reactNativeViewId.toString())
            .commit()


 */