package com.livelike.demo.widget

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.widget.LinearLayout
import com.livelike.demo.R
import com.livelike.demo.databinding.FcCustomAskAWidgetBinding
import com.livelike.engagementsdk.widget.widgetModel.TextAskWidgetModel

class CustomTextAskWidgetView : LinearLayout {

    var askWidgetModel: TextAskWidgetModel? = null
    private lateinit var binding: FcCustomAskAWidgetBinding

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
        binding = FcCustomAskAWidgetBinding.bind(inflate(context, R.layout.fc_custom_ask_a_widget, this))
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        registerListeners()
        askWidgetModel?.widgetData?.let { liveLikeWidget ->
//            titleTv.text = liveLikeWidget.title
//            promptTv.text = liveLikeWidget.prompt
//            confirmationMessageTv.text = liveLikeWidget.confirmation_message
//            confirmationMessageTv.visibility = GONE
        }
    }


    private fun registerListeners(): Unit {

        binding.influencerQuestionInput.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(arg0: Editable) {

                // Updating Send Button
                binding.sendBtn.isEnabled = binding.influencerQuestionInput.text?.length!! > 0
                val bgColor = when (binding.sendBtn.isEnabled) {
                    true -> R.color.ask_a_widget_active_input
                    false -> R.color.ask_a_widget_in_active_input
                }
                binding.sendBtn.setBackgroundColor(bgColor)

                // Updating Word Count
                binding.wordCount.text = "${binding.influencerQuestionInput.text?.length}/${R.integer.ask_influencer_input_word_limit}"
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
//                    TODO: DO this later
//                    disableUserInput()// user input edit text disbaled
//                    disableSendBtn() // send button disbaled
//                    askWidgetModel?.submitReply(userInputEdt.text.toString().trim())
//                    hideKeyboard()
//                    confirmationMessageTv.visibility = VISIBLE
            }
        }
    }
}