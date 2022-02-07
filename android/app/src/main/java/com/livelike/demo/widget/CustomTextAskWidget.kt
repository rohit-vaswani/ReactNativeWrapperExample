package com.livelike.demo.widget

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.livelike.demo.R
import com.livelike.demo.databinding.FcCustomAskAWidgetBinding
import com.livelike.engagementsdk.widget.widgetModel.TextAskWidgetModel

class CustomTextAskWidget : LinearLayout {

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
        binding = FcCustomAskAWidgetBinding.bind(inflate(context, R.layout.fc_widget_view, this))
        addView(binding.root)
        Log.i("View", "Added")
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

        Log.i("Listeners Registered", "Added")

        // Text Change on input box
        binding.influenceQuestionInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(arg0: Editable) {
                if (!binding.sendBtn.isEnabled) {
                    binding.sendBtn.isEnabled = true
                }
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
            if (binding.influenceQuestionInput.text.toString().trim().isNotEmpty()) {

                askWidgetModel?.submitReply(binding.influenceQuestionInput.text.toString().trim())


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