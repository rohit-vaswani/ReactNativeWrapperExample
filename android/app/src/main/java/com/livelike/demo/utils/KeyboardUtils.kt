package com.livelike.demo.utils

import android.content.Context
import android.os.IBinder
import android.view.inputmethod.InputBinding
import android.view.inputmethod.InputMethodManager

class KeyboardUtils {

    companion object {
        fun showKeyboard(context: Context) {
//            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
//            imm?.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0)
        }

        fun dismissKeyboard(context: Context, windowToken: IBinder?) {
//            val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
//            inputMethodManager?.hideSoftInputFromWindow(windowToken, 0)
//            val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//            inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
        }
    }


}
