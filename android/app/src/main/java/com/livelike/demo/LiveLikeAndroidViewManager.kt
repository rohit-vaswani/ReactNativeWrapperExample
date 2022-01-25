package com.livelike.demo
import android.view.Choreographer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.ViewGroupManager
import com.facebook.react.uimanager.annotations.ReactPropGroup
import com.livelike.demo.ui.main.FCChatView


/*
    replace with your package
    Write a Wrapper for ChatView and convert Fragment Logic/lifecycle into View lifecycle.
    Along with View, expose a Module whose singleton instance will be created on RN SIDE AND initialized.
 */


class LiveLikeAndroidViewManager(var reactContext: ReactApplicationContext) : ViewGroupManager<FrameLayout?>() {
    private var propWidth = 0
    private var propHeight = 0


    // TODO: Remove the hard-coding
    private val programId = "08c5c27e-952d-4392-bd2a-c042db036ac5"
    private val chatRoomId = "32d1d38b-6321-4f45-ab38-05750792547d"
    private val clientId = "OPba08mrr8gLZ2UMQ3uWMBOLiGhfovgIeQAEfqgI"

    private lateinit var chatViewConstraintLayout: FCChatView

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
            COMMAND_CREATE -> addChatView(root, reactNativeViewId)
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
    private fun addChatView(root: FrameLayout, reactNativeViewId: Int) {
        val parentView = root.findViewById<View>(reactNativeViewId) as ViewGroup
        addChatFrame(parentView)
    }


    private fun addChatFrame(parentView: ViewGroup) {
        Choreographer.getInstance().postFrameCallback(object : Choreographer.FrameCallback {
            override fun doFrame(frameTimeNanos: Long) {
                manuallyLayoutChildren(parentView)
                parentView.viewTreeObserver.dispatchOnGlobalLayout()
                Choreographer.getInstance().postFrameCallback(this)
                val view: FCChatView = LayoutInflater.from(reactContext.currentActivity).inflate(R.layout.fc_chat_view, parentView, false) as FCChatView
                parentView.addView(view)
                view.initialize(clientId, programId, chatRoomId)
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
    }
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