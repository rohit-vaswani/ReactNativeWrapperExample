package com.livelike.demo.ui.main

import android.content.Context
import android.graphics.Outline
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.livelike.demo.R
import com.livelike.demo.databinding.CustomMsgItemBinding
import com.livelike.demo.databinding.FcChatViewBinding
import com.livelike.engagementsdk.DismissAction

class VideoView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {


    private var inflated = false
    private var mediaPlayer: MediaPlayer? = null
    private var isMuted: Boolean = false
    private var playedAtLeastOnce: Boolean = false
    private var stopPosition: Int = 0
    var _binding: CustomMsgItemBinding? = null
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
        if (!inflated) {
            inflated = true
            _binding = CustomMsgItemBinding.bind(
                inflate(
                    context,
                    R.layout.custom_msg_item,
                    this@VideoView
                )
            )
        }
        _binding?.playbackErrorView?.visibility = View.GONE
        if (!videoUrl.isNullOrEmpty()) {
            videoUrl?.let {
                setFrameThumbnail(videoUrl!!)
            }

        }
        setOnClickListeners()
    }


    /** sets the listeners */
    private fun setOnClickListeners() {
        _binding?.let {
            it.soundView.setOnClickListener {
                if (isMuted) {
                    unMute()
                } else {
                    mute()
                }
            }

            it.widgetContainer.setOnClickListener { view ->
                if (it.playerView?.isPlaying) {
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
    }


    /** sets the video view */
    private fun initializePlayer(videoUrl: String) {
        _binding?.let {
            try {
                val uri = Uri.parse(videoUrl)
                it.playerView.setVideoURI(uri)
                // playerView.seekTo(stopPosition)
                it.playerView.requestFocus()
                it.playerView.start()
                unMute()

                // perform set on prepared listener event on video view
                try {
                    it.playerView.setOnPreparedListener { mp ->
                        // do something when video is ready to play
                        this.mediaPlayer = mp
                        playedAtLeastOnce = true
                        it.progressBar.visibility = View.GONE
                        it.playbackErrorView.visibility = View.GONE
                        it.soundView.visibility = VISIBLE
                        it.icSound.visibility = VISIBLE
                    }

                    it.playerView.setOnCompletionListener { mediaPlayer ->
                        it.playerView?.stopPlayback()
                        it.soundView.visibility = GONE
                        setFrameThumbnail(videoUrl)
                    }



                    it.playerView.setOnErrorListener { _, what, extra ->
                        //logError { "Error on playback" }
                        it.progressBar.visibility = GONE
                        it.icPlay.visibility = GONE
                        it.playerView.visibility = INVISIBLE
                        it.playbackErrorView.visibility = VISIBLE
                        it.soundView.visibility = GONE
                        true
                    }
                } catch (e: Exception) {
                    it.progressBar.visibility = GONE
                    it.playbackErrorView.visibility = VISIBLE
                    e.printStackTrace()
                }
            } catch (e: Exception) {
                it.progressBar.visibility = GONE
                it.playbackErrorView.visibility = VISIBLE
                e.printStackTrace()
            }
        }
    }

    /** responsible for playing the video */
    private fun play() {
        _binding?.let {
            it.progressBar.visibility = View.VISIBLE
            it.icPlay.visibility = View.GONE
            it.playbackErrorView.visibility = View.GONE
            it.thumbnailView.visibility = View.GONE
            it.playerView.visibility = View.VISIBLE
            videoUrl?.let { url -> initializePlayer(url) }
        }
    }

    /** responsible for resuming the video from where it was stopped */
    private fun resume() {
        _binding?.let {
            it.soundView.visibility = VISIBLE
            it.playbackErrorView.visibility = GONE
            it.progressBar.visibility = GONE
            it.icPlay.visibility = GONE
            it.playerView.seekTo(stopPosition)
            if (it.playerView.currentPosition == 0) {
                play()
            } else {
                it.playerView.start()
            }
        }
    }

    /** responsible for stopping the video */
    private fun pause() {
        _binding?.let {
            stopPosition = it.playerView.currentPosition
            it.playerView.pause()
            it.soundView.visibility = GONE
            it.icPlay.visibility = View.VISIBLE
            it.playbackErrorView.visibility = View.GONE
            it.icPlay.setImageResource(R.drawable.ic_play_button)
        }
    }

    /** responsible for stopping the player and releasing it */
    private fun release() {
        try {
            playedAtLeastOnce = false
            _binding?.let {
                if (it.playerView != null && it.playerView.isPlaying) {
                    it.playerView.stopPlayback()
                    it.playerView.seekTo(0)
                    stopPosition = 0
                    mediaPlayer?.stop()
                    mediaPlayer?.release()
                    mediaPlayer = null
                }
            }
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }

    /** checks if the player is paused */
    fun isPaused(): Boolean {
        return !mediaPlayer!!.isPlaying && playedAtLeastOnce
    }

    /** mutes the video */
    private fun mute() {
        try {
            isMuted = true
            mediaPlayer?.setVolume(0f, 0f)
            _binding?.let {
                it.icSound.setImageResource(R.drawable.ic_volume_on)
                it.muteTv.text = context.resources.getString(R.string.livelike_unmute_label)
            }
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }

    /** unmute the video */
    private fun unMute() {
        try {
            isMuted = false
            mediaPlayer?.setVolume(1f, 1f)
            _binding?.let {
                it.icSound.setImageResource(R.drawable.ic_volume_off)
                it.muteTv.text = context.resources.getString(R.string.livelike_mute_label)
            }

        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }

    /** extract thumbnail from the video url */
    private fun setFrameThumbnail(videoUrl: String) {
        _binding?.let {
            it.thumbnailView.visibility = VISIBLE
            it.icPlay.visibility = VISIBLE
            it.progressBar.visibility = GONE
            it.playbackErrorView.visibility = GONE
            it.icPlay.setImageResource(R.drawable.ic_play_button)
            it.playerView.visibility = INVISIBLE
            var requestOptions = RequestOptions()

            if (videoUrl.isNotEmpty()) {
                Glide.with(context.applicationContext)
                    .asBitmap()
                    .load(videoUrl)
                    .apply(requestOptions)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .thumbnail(0.1f)
                    .into(it.thumbnailView)
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setPlayerViewCornersRound(isOnlyBottomCornersToBeRounded: Boolean) {
        _binding?.let {
            it.playerView.outlineProvider = object : ViewOutlineProvider() {
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

            it.playerView.clipToOutline = true
        }

    }
}