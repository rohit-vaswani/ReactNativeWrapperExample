package com.livelike.demo
import android.util.Log
import android.view.Choreographer
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.FragmentActivity
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.ViewGroupManager
import com.facebook.react.uimanager.annotations.ReactPropGroup
import com.livelike.demo.ui.main.ChatFragment


/*
    replace with your package
    Write a Wrapper for ChatView and convert Fragment Logic/lifecycle into View lifecycle.
    Along with View, expose a Module whose singleton instance will be created on RN SIDE AND initialized.
 */


class LiveLikeAndroidViewManager(var reactContext: ReactApplicationContext) :
    ViewGroupManager<FrameLayout?>() {
    private var propWidth = 0
    private var propHeight = 0
    private val programId = "3ebd6f09-2f16-4171-b94a-c9335154d672" // channelId // roomId
    // chang userID: 64b501fe-b084-46c2-8639-c7f2d69c8a11
    // Loyal HAWK userID: 8f52892d-3530-490d-b838-1594d296e7c9
    // Royal Legend userID: 4a2bbc1e-be12-40a9-83a9-514473a34c94 [Current User ID]*
    // Leaderboard ID: 86ef1ca9-5ebf-4f8f-8a4b-a4a34697bc47 (Loyal Hawk?)
    private val isChatVisible = false
    private lateinit var chatFragment: ChatFragment

    override fun getName(): String {
        return REACT_CLASS
    }

    /**
     * Return a FrameLayout which will later hold the Fragment
     */
    public override fun createViewInstance(reactContext: ThemedReactContext): FrameLayout {
        return FrameLayout(reactContext)
    }

    /**
     * Map the "create" command to an integer
     */
    override fun getCommandsMap(): Map<String, Int>? {
        val hashMap = hashMapOf<String, Int>()
        hashMap.apply {
            put("create", COMMAND_CREATE)
            put("sendMessage", COMMAND_SEND_MESSAGE)
        }
        return hashMap
    }

    /**
     * Handle "create" command (called from JS) and call createFragment method
     */
    override fun receiveCommand(
        root: FrameLayout,
        commandId: String,
        args: ReadableArray?
    ) {
        super.receiveCommand(root, commandId, args)
        val reactNativeViewId = args!!.getInt(0)
        when (commandId.toInt()) {
            COMMAND_CREATE -> createFragment(root, reactNativeViewId)
            COMMAND_SEND_MESSAGE -> sendChatMessage(args)
            else -> {}
        }
    }

    @ReactPropGroup(names = ["width", "height"], customType = "Style")
    fun setStyle(view: FrameLayout?, index: Int, value: Int) {
        if (index == 0) {
            propWidth = value
        }
        if (index == 1) {
            propHeight = value
        }
    }

    /**
     * Replace your React Native view with a custom fragment
     */
    fun createFragment(root: FrameLayout, reactNativeViewId: Int) {
        val parentView = root.findViewById<View>(reactNativeViewId) as ViewGroup
        setupLayout(parentView)
        chatFragment = ChatFragment.newInstance(programId, isChatVisible)
        // val myFragment = WidgetTimeLineFragment()
        val activity = reactContext.currentActivity as FragmentActivity?
        activity!!.supportFragmentManager
            .beginTransaction()
            .replace(reactNativeViewId, chatFragment, reactNativeViewId.toString())
            .commit()
    }

    private fun sendChatMessage(args: ReadableArray?) {

        val message = args?.getString(1).toString()
        Log.i("MESSAGE arg", message)
        chatFragment.sendChatMessage(message)


    }

    fun setupLayout(view: View) {
        Choreographer.getInstance().postFrameCallback(object : Choreographer.FrameCallback {
            override fun doFrame(frameTimeNanos: Long) {
                manuallyLayoutChildren(view)
                view.viewTreeObserver.dispatchOnGlobalLayout()
                Choreographer.getInstance().postFrameCallback(this)
            }
        })
    }

    /**
     * Layout all children properly
     */
    fun manuallyLayoutChildren(view: View) {
        // propWidth and propHeight coming from react-native props
        val width = propWidth
        val height = propHeight
        view.measure(
            View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)
        )
        view.layout(0, 0, width, height)
    }

    companion object {
        const val REACT_CLASS = "LiveLikeAndroidViewManager"
        const val COMMAND_CREATE = 0
        const val COMMAND_SEND_MESSAGE = 1
    }
}