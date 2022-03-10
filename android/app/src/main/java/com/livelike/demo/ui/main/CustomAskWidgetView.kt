package com.livelike.demo.ui.main

import android.content.Context
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.livelike.demo.R
import com.livelike.demo.databinding.FcCustomAskAWidgetBinding
import com.livelike.engagementsdk.widget.widgetModel.TextAskWidgetModel


class CustomAskWidgetView : LinearLayout {

    var askWidgetModel: TextAskWidgetModel? = null
    private lateinit var binding: FcCustomAskAWidgetBinding
    lateinit var userEventsListener: UserEventsListener
    var influencerName: String? = null

    interface UserEventsListener {
        fun closeDialog()
        fun onMessageSent(message: String)
    }

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(attrs, defStyle)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        binding = FcCustomAskAWidgetBinding.bind(
            inflate(
                context,
                R.layout.fc_custom_ask_a_widget,
                this
            )
        )
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        registerListeners()
        askWidgetModel?.widgetData?.let { liveLikeWidget -> }
        binding.closeIconBtn.visibility = View.VISIBLE
        this.influencerName?.let {
            binding.headerTitle.text = "Ask ${it}"
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        closeDialog()
    }


    private fun registerListeners(): Unit {


        binding.influencerQuestionInput.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(arg0: Editable) {

                // Updating Send Button
                val isEnabled = binding.influencerQuestionInput.text?.length!! > 0

                // Updating Send Button
                binding.sendBtn.isEnabled = isEnabled

                // Updating Word Count
                val maxWordCount = resources.getInteger(R.integer.ask_influencer_input_word_limit)
                binding.wordCount.text = "${binding.influencerQuestionInput.text?.length}/${maxWordCount}"
            }

            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })


        // On Send Button
        binding.sendBtn.setOnClickListener {
            if (binding.influencerQuestionInput.text.toString().trim().isNotEmpty()) {
                askWidgetModel?.submitReply(binding.influencerQuestionInput.text.toString().trim())
                binding.askInfluencerContentWrapper.visibility = View.GONE
                binding.askInfluencerConfirmationWrapper.visibility = View.VISIBLE
                binding.closeIconBtn.visibility = View.GONE
                val influencerName = if(this.influencerName.isNullOrEmpty()) "our Expert" else this.influencerName
                binding.confirmationMessage.text =  "We've shared your message with ${influencerName}"
                userEventsListener.onMessageSent(binding.influencerQuestionInput.text.toString())
                Handler().postDelayed({
                    closeDialog()
                }, 2500)
            }
        }


        binding.closeIconBtn.setOnClickListener {
            closeDialog()
        }
    }

    private fun closeDialog() {
        binding.influencerQuestionInput.clearFocus()
        userEventsListener.closeDialog()
    }
}