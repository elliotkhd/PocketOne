package com.example.pocketcasts.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import com.example.pocketcasts.data.Episode
import com.example.pocketcasts.data.MyDatabase
import com.example.pocketcasts.util.SPUtil

class MusicService : Service() {
    private val mediaPlayer: MediaPlayer by lazy { MediaPlayer() }

    private lateinit var pref: SharedPreferences
    private var isMediaPlayerUsed = false
    var speed: Float = 1.5f

    override fun onCreate() {
        super.onCreate()
        pref = getSharedPreferences("default", Context.MODE_PRIVATE)
    }

    inner class MusicController : Binder() {

        fun play(episode: Episode) {
            if (episode.audioUrl != getCurrentEpisode()?.audioUrl) {
                saveProgress()
                SPUtil.getInstance().saveEpisodeId(episode.id!!)
                mediaPlayer.reset()
                mediaPlayer.setDataSource(episode.audioUrl)
                mediaPlayer.prepareAsync()
                mediaPlayer.setOnPreparedListener {
                    SPUtil.getInstance().enablePlaying()
                    mediaPlayer.seekTo(getCurrentEpisode()?.progress ?: 0)
                    mediaPlayer.playbackParams = mediaPlayer.playbackParams.setSpeed(speed)
                    mediaPlayer.start()
                }
                isMediaPlayerUsed = true
            } else {
                if (isMediaPlayerUsed) {
                    SPUtil.getInstance().enablePlaying()
                    mediaPlayer.start()
                } else {
                    mediaPlayer.reset()
                    mediaPlayer.setDataSource(episode.audioUrl)
                    mediaPlayer.prepareAsync()
                    mediaPlayer.setOnPreparedListener {
                        SPUtil.getInstance().enablePlaying()
                        mediaPlayer.seekTo(episode.progress)
                        mediaPlayer.playbackParams = mediaPlayer.playbackParams.setSpeed(speed)
                        mediaPlayer.start()
                    }
                    isMediaPlayerUsed = true
                }
            }
        }

        fun pause() = mediaPlayer.pause()

        fun saveProgress() {
            val progress =
                if (mediaPlayer.currentPosition - 3000 > 0)
                    mediaPlayer.currentPosition - 3000 else 0
            val tmpId = SPUtil.getInstance().getEpisodeId()
            MyDatabase.getInstance().episodeDao().updateProgress(tmpId, progress)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return MusicController()
    }

    override fun onUnbind(intent: Intent?): Boolean {
        if (isMediaPlayerUsed) MusicController().saveProgress()
        mediaPlayer.release()
        return false
    }

    private fun getCurrentEpisode(): Episode? {
        val tmpId = SPUtil.getInstance().getEpisodeId()
        return MyDatabase.getInstance().episodeDao()
            .findEpisodeById(tmpId)
    }
}