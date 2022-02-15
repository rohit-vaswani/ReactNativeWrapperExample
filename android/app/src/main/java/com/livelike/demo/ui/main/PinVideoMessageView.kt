package com.livelike.demo.ui.main

import android.content.Context
import android.graphics.Outline
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.livelike.demo.R
import com.livelike.engagementsdk.DismissAction


class PinVideoMessageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
):  LinearLayout(context, attrs, defStyleAttr) {

    private var mediaPlayer: MediaPlayer? = null
    private var isMuted: Boolean = false
    private var playedAtLeastOnce: Boolean = false
    private var stopPosition: Int = 0
    lateinit var soundView:LinearLayout
    lateinit var playbackErrorView:LinearLayout
    lateinit var thumbnailView: ImageView
    lateinit var widgetContainer: ConstraintLayout
    lateinit var playerView: VideoView
    lateinit var progressBar: ProgressBar
    lateinit var icPlay:ImageView
    lateinit var icSound:ImageView
    lateinit var muteTv: TextView
    lateinit var chatNickname:TextView
    lateinit var imgChatAvatar:ImageView
    var thumbnailUrl: String? = null

    fun setVideoThumbnail(url: String?){
        this.thumbnailUrl = url
    }

    fun initViews() {
        soundView = findViewById<LinearLayout>(R.id.sound_view)
        playbackErrorView = findViewById<LinearLayout>(R.id.playbackErrorView)
        thumbnailView = findViewById<ImageView>(R.id.thumbnailView)
        widgetContainer = findViewById<ConstraintLayout>(R.id.widgetContainer)
        playerView = findViewById<VideoView>(R.id.playerView)
        progressBar = findViewById<ProgressBar>(R.id.progress_bar)
        icPlay = findViewById<ImageView>(R.id.ic_play)
        icSound = findViewById<ImageView>(R.id.ic_sound)
        muteTv = findViewById<TextView>(R.id.mute_tv)
        chatNickname = findViewById<TextView>(R.id.chat_nickname)
        imgChatAvatar = findViewById<ImageView>(R.id.img_chat_avatar)
    }
    var videoUrl: String? = null
        set(value) {
            field = value
            if (value != null) {
                setFrameThumbnail(value)
            }
        }
    var dismissFunc: ((action: DismissAction) -> Unit)? =
        {
            removeAllViews()
        }
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        inflate(context)
    }
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        release()
    }
    private fun inflate(context: Context) {
        playbackErrorView.visibility = View.GONE
        thumbnailView.clipToOutline = true
        if (!videoUrl.isNullOrEmpty()) {
            videoUrl?.let {
                setFrameThumbnail(videoUrl!!)
            }
        }
        setOnClickListeners()
    }
    /** sets the listeners */
    private fun setOnClickListeners() {
        soundView.setOnClickListener {
            if (isMuted) {
                unMute()
            } else {
                mute()
            }
        }
        widgetContainer.setOnClickListener { view ->
            if (playerView.isPlaying) {
                pause()
            } else {
                if (stopPosition > 0) { // already running
                    resume()
                } else {
                    play()
                }
            }
        }
    }
    /** sets the video view */
    private fun initializePlayer(videoUrl: String) {
        try {
            val uri = Uri.parse(videoUrl)
            playerView.clipToOutline = true
            playerView.setVideoURI(uri)
            playerView.requestFocus()
            playerView.start()
            unMute()
            // perform set on prepared listener event on video view
            try {
                playerView.setOnPreparedListener { mp ->
                    this.mediaPlayer = mp
                    playedAtLeastOnce = true
                    progressBar.visibility = View.GONE
                    playbackErrorView.visibility = View.GONE
                }
                playerView.setOnCompletionListener { mediaPlayer ->
                    playerView.stopPlayback()
                    setFrameThumbnail(videoUrl)
                    soundView.visibility = LinearLayout.GONE
                }
                playerView.setOnErrorListener { _, what, extra ->
                    hideVideoControls()
                    progressBar.visibility = LinearLayout.GONE
                    playerView.visibility = LinearLayout.INVISIBLE
                    playbackErrorView.visibility = LinearLayout.VISIBLE
                    true
                }
            } catch (e: Exception) {
                progressBar.visibility = LinearLayout.GONE
                playbackErrorView.visibility = LinearLayout.VISIBLE
                e.printStackTrace()
            }
        } catch (e: Exception) {
            progressBar.visibility = LinearLayout.GONE
            playbackErrorView.visibility = LinearLayout.VISIBLE
            e.printStackTrace()
        }
    }
    private fun showVideoControls(){
        icPlay.setImageResource(R.drawable.ic_play_button)
        icPlay.visibility = LinearLayout.VISIBLE
        playbackErrorView.visibility = LinearLayout.GONE
        soundView.visibility = LinearLayout.VISIBLE
    }
    private fun hideVideoControls() {
        icPlay.setImageResource(R.drawable.ic_play_button)
        icPlay.visibility = LinearLayout.GONE
        playbackErrorView.visibility = LinearLayout.GONE
        soundView.visibility = LinearLayout.GONE
    }
    /** responsible for playing the video */
    private fun play() {
        progressBar.visibility = View.VISIBLE
        hideVideoControls()
        playbackErrorView.visibility = View.GONE
        thumbnailView.visibility = View.GONE
        playerView.visibility = View.VISIBLE
        videoUrl?.let { url -> initializePlayer(url) }
    }
    /** responsible for resuming the video from where it was stopped */
    private fun resume() {
        playbackErrorView.visibility = LinearLayout.GONE
        progressBar.visibility = LinearLayout.GONE
        playerView.seekTo(stopPosition)
        hideVideoControls()
        if (playerView.currentPosition == 0) {
            play()
        } else {
            playerView.start()
        }
    }
    /** responsible for stopping the video */
    private fun pause() {
        stopPosition = playerView.currentPosition
        playerView.pause()
        showVideoControls()
    }
    /** responsible for stopping the player and releasing it */
    private fun release() {
        try {
            playedAtLeastOnce = false
            if (playerView != null && playerView.isPlaying) {
                playerView.stopPlayback()
                playerView.seekTo(0)
                stopPosition = 0
                mediaPlayer?.stop()
                mediaPlayer?.release()
                mediaPlayer = null
            }
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }
    /** checks if the player is paused */
    fun isPaused(): Boolean {
        return !mediaPlayer!!.isPlaying && playedAtLeastOnce
    }
    private fun mute() {
        try {
            isMuted = true
            mediaPlayer?.setVolume(0f, 0f)
            icSound.setImageResource(R.drawable.ic_volume_on)
            muteTv.text = context.resources.getString(R.string.livelike_unmute_label)
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }
    private fun unMute() {
        try {
            isMuted = false
            mediaPlayer?.setVolume(1f, 1f)
            icSound.setImageResource(R.drawable.ic_volume_off)
            muteTv.text = context.resources.getString(R.string.livelike_mute_label)
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }
    /** extract thumbnail from the video url */
    private fun setFrameThumbnail(videoUrl: String) {
        thumbnailView.visibility = LinearLayout.VISIBLE
        showVideoControls()
        progressBar.visibility = LinearLayout.GONE
        playbackErrorView.visibility = LinearLayout.GONE
        playerView.visibility = LinearLayout.INVISIBLE
        var requestOptions = RequestOptions()

        if (videoUrl.isNotEmpty()) {
            Glide.with(context.applicationContext)
                .asBitmap()
                .load(videoUrl)
                .apply(requestOptions)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .thumbnail(0.1f)
                .into(thumbnailView)
        }

        thumbnailUrl?.let {
            Glide.with(context.applicationContext)
                .load(it)
                .apply(requestOptions)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(thumbnailView)
        }

    }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setPlayerViewCornersRound(isOnlyBottomCornersToBeRounded: Boolean) {
        playerView.outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                val corner = 20f
                if (isOnlyBottomCornersToBeRounded) {
                    outline.setRoundRect(0, -corner.toInt(), view.width, view.height, corner)
                } else {
                    outline.setRoundRect(
                        0,
                        0,
                        view.width,
                        view.height,
                        corner
                    ) // for making all corners rounded
                }
            }
        }
        playerView.clipToOutline = true
    }
}