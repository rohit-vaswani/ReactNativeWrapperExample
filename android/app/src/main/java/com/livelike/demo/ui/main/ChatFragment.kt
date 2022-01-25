package com.livelike.demo.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.livelike.demo.R
import com.livelike.engagementsdk.EngagementSDK
import com.livelike.engagementsdk.EpochTime
import com.livelike.engagementsdk.MessageListener
import com.livelike.engagementsdk.chat.ChatView
import com.livelike.engagementsdk.chat.LiveLikeChatSession
import com.livelike.engagementsdk.publicapis.ErrorDelegate
import com.livelike.engagementsdk.publicapis.LiveLikeCallback
import com.livelike.engagementsdk.publicapis.LiveLikeChatMessage

/**
 * A placeholder fragment containing a simple view.
 */
class ChatFragment : BaseFragment() {

    private lateinit var pageViewModel: PageViewModel
    private var programId = ""
    private var chatRoomId = ""
    private var isChatInputVisible = false
    var chatView: ChatView? = null
    private lateinit var chatSession: LiveLikeChatSession

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageViewModel = ViewModelProvider(requireActivity()).get(PageViewModel::class.java)
        programId = arguments?.getString(ARG_PROGRAM_ID) ?: ""
        chatRoomId = arguments?.getString(ARG_CHAT_ROOM_ID) ?: ""
        isChatInputVisible = arguments?.getBoolean(ARG_CHAT_INPUT_VISIBILITY) ?: false
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.chat_fragment_layout, container, false)
        val chatView: ChatView = root.findViewById(R.id.chat_view)
        initChatSession(chatView)
        return root
    }

    private fun createChatSession(): LiveLikeChatSession? {
        chatSession = pageViewModel.engagementSDK.createChatSession(object : EngagementSDK.TimecodeGetter {
                override fun getTimecode(): EpochTime {
                    return EpochTime(0)
                }

            }, errorDelegate = object : ErrorDelegate() {
                override fun onError(error: String) {
                    Log.e("TEST", error)
                }
            })

        return chatSession
    }

    fun sendChatMessage(message: String) {
        chatSession.sendChatMessage(
            message,
            "",
            0,
            0,
            liveLikeCallback = object : LiveLikeCallback<LiveLikeChatMessage>() {
                override fun onResponse(result: LiveLikeChatMessage?, error: String?) {
                    if (error != null) {
//                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                    } else {
                        //use ChatMessage model class
                    }
                }
            }
        )
    }

    private fun registerMessageListener(chatSession: LiveLikeChatSession) {
        chatSession.setMessageListener(object : MessageListener {

            override fun onNewMessage(message: LiveLikeChatMessage) {
                Log.i("NEW MESSAGE", message.id.toString())
            }


            override fun onHistoryMessage(
                messages: List<LiveLikeChatMessage>
            ) {
                messages.map {
                    it.id
                    Log.i("HISTORY MESSAGE", it.id.toString())
                }
            }

            override fun onDeleteMessage(messageId: String) {

            }

        })

    }

    private fun connectToChatRoom(chatSession: LiveLikeChatSession) {
        chatSession.connectToChatRoom(this.chatRoomId, callback = object : LiveLikeCallback<Unit>() {
            override fun onResponse(result: Unit?, error: String?) {
                if (error != null) {
                    Log.e("TEST", error)
                }
            }
        })
    }


    private fun initChatSession(chatView: ChatView) {
        pageViewModel.chatFrag = this
        createChatSession()

        if (chatSession != null) {
            connectToChatRoom(chatSession)
            registerMessageListener(chatSession)
            chatView.allowMediaFromKeyboard = true
            chatView.isChatInputVisible = true
            chatView.setSession(chatSession)
            //chat_view.clearSession()
            this.chatView = chatView
            //chatSession.close()
        }
    }


    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private const val ARG_PROGRAM_ID = "program_id"
        private const val ARG_CHAT_INPUT_VISIBILITY = "chat_input_visibility"
        private const val ARG_CHAT_ROOM_ID = "chat_room_id"

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        @JvmStatic
        fun newInstance(
            programId: String,
            chatRoomId: String,
            isChatInputVisible: Boolean
        ): ChatFragment {
            return ChatFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PROGRAM_ID, programId)
                    putBoolean(ARG_CHAT_INPUT_VISIBILITY, isChatInputVisible)
                    putString(ARG_CHAT_ROOM_ID, chatRoomId)
                }
            }
        }
    }
}


/*
    Trial code


        pageViewModel.engagementSDK.updateChatNickname("TEST 123")
        chatSession.shouldDisplayAvatar = true
        chatSession.avatarUrl = "https://d13ir53smqqeyp.cloudfront.net/flags/cr-flags/FC-CHC@2x.png"
        pageViewModel.engagementSDK.updateChatNickname("TEST 123")
        chatView.isChatInputVisible = true

        private fun createContentSession() {
            val contentSession = pageViewModel.engagementSDK.createContentSession(this.programId, object : ErrorDelegate() {
                override fun onError(error: String) {
                    Log.i("Check this", error)
                }
            })
            contentSession.chatSession.avatarUrl = "https://d13ir53smqqeyp.cloudfront.net/flags/cr-flags/FC-CHC@2x.png"

        }

        private fun configureAvatar() {
            chatSession.avatarUrl = "https://d13ir53smqqeyp.cloudfront.net/flags/cr-flags/FC-CHC@2x.png"
            chatSession.avatarUrl = null
        }

            private fun registerOnChatRoomUpdate() {
        chatSession.setChatRoomListener(object : ChatRoomListener {
            override fun onChatRoomUpdate(chatRoom: ChatRoomInfo) {
                Log.i("chatRoom.contentFilter", chatRoom.contentFilter.toString())
            }
        })
    }


 */