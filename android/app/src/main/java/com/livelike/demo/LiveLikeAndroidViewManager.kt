package com.livelike.demo
import android.view.Choreographer
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.FragmentActivity
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.common.MapBuilder
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.ViewGroupManager
import com.facebook.react.uimanager.annotations.ReactPropGroup
import com.livelike.demo.ui.main.ChatFragment
import com.livelike.demo.ui.main.WidgetTimeLineFragment


// replace with your package


class LiveLikeAndroidViewManager(var reactContext: ReactApplicationContext) :
    ViewGroupManager<FrameLayout?>() {
    val COMMAND_CREATE = 1
    private var propWidth = 0
    private var propHeight = 0
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
        return MapBuilder.of("create", COMMAND_CREATE)
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
        val commandIdInt = commandId.toInt()
        when (commandIdInt) {
            COMMAND_CREATE -> createFragment(root, reactNativeViewId)
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
        //val myFragment = WidgetTimeLineFragment()
        val myFragment = ChatFragment()
        val activity = reactContext.currentActivity as FragmentActivity?
        activity!!.supportFragmentManager
            .beginTransaction()
            .replace(reactNativeViewId, myFragment, reactNativeViewId.toString())
            .commit()
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
    }
}