package com.livelike.demo.widget

import android.view.Choreographer
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.facebook.react.bridge.*
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.events.RCTEventEmitter
import com.livelike.demo.LiveLikeManager
import com.livelike.demo.R
import com.livelike.demo.ui.main.CustomAskWidgetView
import com.livelike.engagementsdk.LiveLikeContentSession
import com.livelike.engagementsdk.LiveLikeWidget
import com.livelike.engagementsdk.chat.data.remote.LiveLikePagination
import com.livelike.engagementsdk.core.services.messaging.proxies.LiveLikeWidgetEntity
import com.livelike.engagementsdk.core.services.messaging.proxies.WidgetInterceptor
import com.livelike.engagementsdk.publicapis.LiveLikeCallback
import com.livelike.engagementsdk.widget.LiveLikeWidgetViewFactory
import com.livelike.engagementsdk.widget.view.WidgetView
import com.livelike.engagementsdk.widget.widgetModel.*


/*
    TODO:
        1. Register Dismiss on sendButton Click.
        2. Correct the UI for the confirmation.
        3. Register dismiss on the cross button
        4. Change the colour of the userName for video posting

 */

class LiveLikeWidgetView(
    val context: ThemedReactContext,
    val applicationContext: ReactApplicationContext
) : LinearLayout(context), LifecycleEventListener {


    var contentSession: LiveLikeContentSession? = null
    lateinit var widgetView: WidgetView;
    lateinit var customAskWidgetView: CustomAskWidgetView
    var widgetDetails: LiveLikeWidget? = null
    var fallback: Choreographer.FrameCallback;
    private var renderWidget = false
    private var influencerNickName: String? = null


    init {
        this.applicationContext.addLifecycleEventListener(this)
        this.fallback = Choreographer.FrameCallback() {
            manuallyLayoutChildren();
            viewTreeObserver.dispatchOnGlobalLayout();
            if(renderWidget) {
                Choreographer.getInstance().postFrameCallback(this!!.fallback)
            }
        }
        Choreographer.getInstance().postFrameCallback(fallback)
        createView()
        registerCustomViewModel()
    }

    private fun fetchPublishedWidgets() {
        contentSession?.getPublishedWidgets(
            LiveLikePagination.FIRST,
            object : LiveLikeCallback<List<LiveLikeWidget>>() {
                override fun onResponse(result: List<LiveLikeWidget>?, error: String?) {

                    if(result?.size!! > 0) {
                        result?.first()?.let {
                            widgetDetails = it
                        }
                    }

                }
            })
    }

    private fun registerWidgetInterceptor() {
        contentSession?.widgetInterceptor = object : WidgetInterceptor() {
            override fun widgetWantsToShow(widgetData: LiveLikeWidgetEntity) {}
        }
    }

    private fun subscribeWidgetStream() {
        contentSession?.widgetStream?.subscribe(this) {
            it?.let {
                widgetDetails = it
            }
        }
    }

    fun hideWidget() {
        this.renderWidget = false
        contentSession?.widgetInterceptor?.dismissWidget()
        widgetView.clearWidget()
        sendEvent(LiveLikeWidgetViewManager.EVENT_WIDGET_HIDDEN, null)
    }


    fun displayAskWidget() {
        widgetDetails?.let {
            renderWidget = true
            this.widgetView.displayWidget(LiveLikeManager.engagementSDK, it)
            Choreographer.getInstance().postFrameCallback(this.fallback)
            sendEvent(LiveLikeWidgetViewManager.EVENT_WIDGET_SHOWN, null)
        }
    }

    private fun createView() {
        val parentView = LayoutInflater.from(context).inflate(R.layout.fc_widget_view, null) as LinearLayout;
        addView(parentView)
        widgetView = parentView.findViewById(R.id.widget_view);
    }

    override fun onHostResume() {
//        contentSession?.resume()
    }

    override fun onHostPause() {
//        contentSession?.pause()
    }

    override fun onHostDestroy() {
//        contentSession?.close()
        this.contentSession = null
    }

    fun updateContentSession(contentSession: LiveLikeContentSession) {
        this.contentSession = contentSession;
        fetchPublishedWidgets()
        registerWidgetInterceptor()
        subscribeWidgetStream()
    }


    private fun registerCustomViewListeners() {
        customAskWidgetView.userEventsListener =
            object : CustomAskWidgetView.UserEventsListener {
                override fun closeDialog() {
                    hideWidget()
                }

                override fun onMessageSent(message: String) {
                    val map = Arguments.createMap()
                    map.putString("message", message)
                    sendEvent(EVENT_INFLUENCER_MESSAGE_SENT, map)
                }
            }
    }


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

    fun setInfluencerName(influencerName: String) {
        this.influencerNickName = influencerName
    }

    fun sendEvent(
        eventName: String,
        params: WritableMap?
    ) {
        val reactContext = this.getContext() as ReactContext;
        reactContext.getJSModule(RCTEventEmitter::class.java)
            .receiveEvent(this.getId(), eventName, params)
    }


    private fun registerCustomViewModel() {
        widgetView.widgetViewFactory = object : LiveLikeWidgetViewFactory {

            override fun createAlertWidgetView(alertWidgetModel: AlertWidgetModel): View? {
                return null
            }

            override fun createCheerMeterView(cheerMeterWidgetModel: CheerMeterWidgetmodel): View? {
                return null
            }

            override fun createImageSliderWidgetView(imageSliderWidgetModel: ImageSliderWidgetModel): View? {
                return null
            }

            override fun createNumberPredictionFollowupWidgetView(
                followUpWidgetViewModel: NumberPredictionFollowUpWidgetModel,
                isImage: Boolean
            ): View? {
                return null
            }

            override fun createNumberPredictionWidgetView(
                numberPredictionWidgetModel: NumberPredictionWidgetModel,
                isImage: Boolean
            ): View? {
                return null
            }

            override fun createPollWidgetView(
                pollWidgetModel: PollWidgetModel,
                isImage: Boolean
            ): View? {
                return null
            }

            override fun createPredictionFollowupWidgetView(
                followUpWidgetViewModel: FollowUpWidgetViewModel,
                isImage: Boolean
            ): View? {
                return null
            }

            override fun createPredictionWidgetView(
                predictionViewModel: PredictionWidgetViewModel,
                isImage: Boolean
            ): View? {
                return null
            }

            override fun createQuizWidgetView(
                quizWidgetModel: QuizWidgetModel,
                isImage: Boolean
            ): View? {
                return null
            }

            override fun createSocialEmbedWidgetView(socialEmbedWidgetModel: SocialEmbedWidgetModel): View? {
                return null
            }

            // Returns a view for customising Ask a Widget
            override fun createTextAskWidgetView(imageSliderWidgetModel: TextAskWidgetModel): View? {
                customAskWidgetView = CustomAskWidgetView(context).apply {
                    askWidgetModel = imageSliderWidgetModel
                    influencerNickName?.let {
                        influencerName = influencerNickName as String
                    }
                }
                registerCustomViewListeners()
                return customAskWidgetView
            }

            override fun createVideoAlertWidgetView(videoAlertWidgetModel: VideoAlertWidgetModel): View? {
                return null
            }
        }
    }


    companion object {
        const val EVENT_INFLUENCER_MESSAGE_SENT = "onInfluencerMessageSent"
    }
}