package com.livelike.demo

import android.os.Build
import com.facebook.react.ReactActivity
import com.livelike.demo.ui.main.PageViewModel
import android.os.Bundle
import androidx.activity.viewModels
import com.facebook.react.ReactActivityDelegate
import expo.modules.ReactActivityDelegateWrapper

class MainActivity : ReactActivity() {

    private val pageViewModel: PageViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        // Set the theme to AppTheme BEFORE onCreate to support
        // coloring the background, status bar, and navigation bar.
        // This is required for expo-splash-screen.
        // setTheme(R.style.AppTheme)
        // pageViewModel.initEngagementSDK(applicationContext = applicationContext)
        super.onCreate(null)
    }

    /**
     * Returns the name of the main component registered from JavaScript.
     * This is used to schedule rendering of the component.
     */
    override fun getMainComponentName(): String? {
        return "main"
    }

    override fun createReactActivityDelegate(): ReactActivityDelegate {
        return ReactActivityDelegateWrapper(
            this,
            ReactActivityDelegate(this, mainComponentName)
        )
    }

    /**
     * Align the back button behavior with Android S
     * where moving root activities to background instead of finishing activities.
     * @see [](https://developer.android.com/reference/android/app/Activity.onBackPressed
    ) */
    override fun invokeDefaultOnBackPressed() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) {
            if (!moveTaskToBack(false)) {
                // For non-root activities, use the default implementation to finish them.
                super.invokeDefaultOnBackPressed()
            }
            return
        }

        // Use the default back button implementation on Android S
        // because it's doing more than {@link Activity#moveTaskToBack} in fact.
        super.invokeDefaultOnBackPressed()
    }

    companion object {
        const val ID_SHARED_PREFS = "stored_ids"
        const val CLIENT_ID_KEY = "saved_client_id"
        const val PROGRAM_ID_KEY = "saved_program_id"
        const val PUBLIC_CHAT_ID_KEY = "saved_public_chat_id"
        const val INFLUENCER_CHAT_ID_KEY = "saved_influencer_id"

    }
}