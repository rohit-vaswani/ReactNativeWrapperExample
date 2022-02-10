package com.livelike.demo.ui.main

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import com.livelike.demo.R
import com.livelike.engagementsdk.EngagementSDK
import com.livelike.engagementsdk.EpochTime
import com.livelike.engagementsdk.chat.ChatView
import com.livelike.engagementsdk.chat.LiveLikeChatSession
import com.livelike.engagementsdk.publicapis.ErrorDelegate
import com.livelike.engagementsdk.publicapis.LiveLikeCallback

/**
 * A placeholder fragment containing a simple view.
 */
class FCChatView : ConstraintLayout {


    private lateinit var pageViewModel: PageViewModel
    private var programId = ""
    private var clientId = ""
    private var chatRoomId = ""
    var chatView: ChatView? = null
    private lateinit var chatSession: LiveLikeChatSession


    constructor(context: Context) : super(context) {}

    constructor(
        context: Context,
        attrs: AttributeSet? = null
    ) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    fun initialize(clientId: String, programId: String, chatRoomId: String) {
        val chatView = this.findViewById<ChatView>(R.id.chat_view)
        this.clientId = clientId
        this.programId = programId
        this.chatRoomId = chatRoomId
        this.chatView = chatView
        this.pageViewModel = PageViewModel()
        initChatSession(chatView)
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

    private fun connectToChatRoom(chatSession: LiveLikeChatSession) {
        chatSession.connectToChatRoom(
            this.chatRoomId,
            callback = object : LiveLikeCallback<Unit>() {
                override fun onResponse(result: Unit?, error: String?) {
                    if (error != null) {
                        Log.e("TEST", error)
                    }
                }
            })
    }


    private fun initChatSession(chatView: ChatView) {

        // TODO: Remove this
        return

        createChatSession()

        if (this.chatSession == null) {
            return
        }

        val chatSession = this.chatSession as LiveLikeChatSession

        connectToChatRoom(chatSession)
        chatView.allowMediaFromKeyboard = true
        chatView.isChatInputVisible = true
        chatView.setSession(chatSession)
        //chat_view.clearSession()
        this.chatView = chatView
        //chatSession.close()
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



    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private const val ARG_PROGRAM_ID = "program_id"
        private const val ARG_CHAT_INPUT_VISIBILITY = "chat_input_visibility"
        private const val ARG_CHAT_ROOM_ID = "chat_room_id"


    }



//        @JvmStatic
//        fun newInstance(
//            programId: String,
//            chatRoomId: String,
//            isChatInputVisible: Boolean
//        ): ChatFragment {
//            return ChatFragment(context = Context, attrs).apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PROGRAM_ID, programId)
//                    putBoolean(ARG_CHAT_INPUT_VISIBILITY, isChatInputVisible)
//                    putString(ARG_CHAT_ROOM_ID, chatRoomId)
//                }
//            }
//        }


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


 */