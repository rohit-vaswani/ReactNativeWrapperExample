package com.livelike.demo.ui.main

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.GsonBuilder
import com.livelike.demo.MainActivity
import com.livelike.engagementsdk.EngagementSDK
import com.livelike.engagementsdk.LiveLikeContentSession
import com.livelike.engagementsdk.chat.data.remote.LiveLikePagination
import com.livelike.engagementsdk.core.AccessTokenDelegate
import com.livelike.engagementsdk.core.data.models.LeaderBoard
import com.livelike.engagementsdk.core.data.models.LeaderBoardEntry
import com.livelike.engagementsdk.core.data.models.LeaderBoardEntryPaginationResult
import com.livelike.engagementsdk.publicapis.ErrorDelegate
import com.livelike.engagementsdk.publicapis.LiveLikeCallback
import com.livelike.engagementsdk.publicapis.LiveLikeUserApi


class PageViewModel : ViewModel() {

//    var chatFrag: ChatFragment? = null
    lateinit var engagementSDK: EngagementSDK
    lateinit var contentSession: LiveLikeContentSession
    val widgetJsonData: MutableLiveData<String> = MutableLiveData()
    val currentUserLeaderBoard: MutableLiveData<LeaderBoardEntry> = MutableLiveData()
    val clientId = "OPba08mrr8gLZ2UMQ3uWMBOLiGhfovgIeQAEfqgI" // Fancode
//    val clientId = "mOBYul18quffrBDuq2IACKtVuLbUzXIPye5S3bq5"

    fun createContentSession(programId: String?) {
        contentSession = programId?.let { engagementSDK.createContentSession(it) }!!
        contentSession.widgetStream.subscribe(this) {
            it?.let {
                val widgetDataJson = GsonBuilder().create().toJson(it)
                widgetJsonData.postValue(widgetDataJson)
            }
        }
    }

    fun initEngagementSDK(applicationContext: Context) {
        val sharedPreferences = applicationContext.getSharedPreferences(
            MainActivity.ID_SHARED_PREFS,
            Context.MODE_PRIVATE
        )
        engagementSDK = clientId?.let {
            EngagementSDK(
                it,
                applicationContext,
                object : ErrorDelegate() {
                    override fun onError(error: String) {
                        println("LiveLikeApplication.onError--->$error")
                    }
                }, accessTokenDelegate = object : AccessTokenDelegate {
                    override fun getAccessToken(): String? {
                        return applicationContext.getSharedPreferences(
                            applicationContext.packageName,
                            Context.MODE_PRIVATE
                        ).getString(
                            PREF_USER_ACCESS_TOKEN,
                            null
                        ).apply {
                            println("Token:$this")
                        }
                    }

                    override fun storeAccessToken(accessToken: String?) {
                        applicationContext.getSharedPreferences(
                            applicationContext.packageName,
                            Context.MODE_PRIVATE
                        ).edit().putString(
                            PREF_USER_ACCESS_TOKEN,
                            accessToken
                        ).apply()
                    }
                })
        }!!



        getUserProfileDetails()
        getUserLeaderBoardDetails()


    }

    private fun getUserProfileDetails() {

        engagementSDK.getCurrentUserDetails(object : LiveLikeCallback<LiveLikeUserApi>() {
            override fun onResponse(result: LiveLikeUserApi?, error: String?) {
                if (result !== null)
                    Log.i("USER DETAILS", result.toString())
            }
        })

    }

    private fun getUserLeaderBoardDetails() {

        //Considering this as end of init calling getCurrentProfileData
        engagementSDK.getLeaderBoardEntryForCurrentUserProfile(
            LEADERBOARD_ID,
            object : LiveLikeCallback<LeaderBoardEntry>() {
                override fun onResponse(result: LeaderBoardEntry?, error: String?) {
                    if (error?.isNotEmpty() != true) {
                        currentUserLeaderBoard.value = result
                    }
                }

            })
    }

    fun initiateLeaderBoard(liveLikeCallback: LiveLikeCallback<List<LeaderBoard>>) {
        engagementSDK.getLeaderBoardsForProgram(
            CONTENT_PROGRAM_ID, liveLikeCallback
        )
    }

    fun getLeaderBoardEntries(liveLikeCallback: LiveLikeCallback<LeaderBoardEntryPaginationResult>) {
        engagementSDK.getEntriesForLeaderBoard(
            LEADERBOARD_ID,
            LiveLikePagination.FIRST,
            liveLikeCallback
        )
    }

    companion object {

        /*const val LIVELIKE_CLIENT_ID: String = "jV2LSongmdccPRL65M8GC6Z8lTievPsRsqsoSMiq"
        const val CONTENT_PROGRAM_ID: String = "9baf1962-f7db-43b9-ae8e-b84f1bf31988"*/


        const val LIVELIKE_CLIENT_ID: String = "mOBYul18quffrBDuq2IACKtVuLbUzXIPye5S3bq5"
        const val CONTENT_PROGRAM_ID: String = "086a57ea-e082-4cd6-a52c-48ab2bbd4ca4"

        /* const val LIVELIKE_CLIENT_ID: String = "mOBYul18quffrBDuq2IACKtVuLbUzXIPye5S3bq5"
         const val CONTENT_PROGRAM_ID: String = "df48a928-90af-43fa-bda0-270f65d597d6"*/

        /*const val LIVELIKE_CLIENT_ID: String = "gD21f6o5DzyXl1rOvitdrvAA6F1XyBlQdU1hyexx"
        const val CONTENT_PROGRAM_ID: String = "9baf1962-f7db-43b9-ae8e-b84f1bf31988"*/


        /*const val LIVELIKE_CLIENT_ID: String = "ufMEjCqZXZDijB3wzOqurOSWKxazZLPDkdnJVdyb"
        const val CONTENT_PROGRAM_ID: String = "4682af13-6793-45f3-bf55-dac529e0af57"*/

        const val LEADERBOARD_ID: String = "8f52892d-3530-490d-b838-1594d296e7c9" // OLD:  "86ef1ca9-5ebf-4f8f-8a4b-a4a34697bc47"
        /*const val LEADERBOARD_ID: String = "dd00dda1-8493-40c8-9f6f-108c58796fe0"*/

        const val PREF_USER_ACCESS_TOKEN = "user_access_token"
    }


}