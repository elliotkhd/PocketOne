package com.example.pocketcasts.util

import android.content.Context

class SPUtil() {


    companion object {
        private const val CURRENT_EPISODE_ID = "CurrentEpisodeId"
        private const val CURRENT_EPISODE_PLAY_STATUS = "CurrentEpisodeStatus"
        private var sp: SPUtil? = null
        fun getInstance() =
            sp ?: synchronized(this) {
                sp ?: SPUtil().also { sp = it }
            }
    }

    private val pref =
        App.instance.applicationContext.getSharedPreferences("default", Context.MODE_PRIVATE)

    fun saveEpisodeId(id: Long) = with(pref.edit()) {
        putLong(CURRENT_EPISODE_ID, id)
        commit()
    }

    fun disablePlaying() = with(pref.edit()) {
        putBoolean(CURRENT_EPISODE_PLAY_STATUS, false)
        commit()
    }

    fun enablePlaying() = with(pref.edit()) {
        putBoolean(CURRENT_EPISODE_PLAY_STATUS, true)
        commit()
    }

    fun getEpisodeId() = pref.getLong(CURRENT_EPISODE_ID, -10)

    fun changeEpisodeStatus() {
        var tmpStatus = pref.getBoolean(CURRENT_EPISODE_PLAY_STATUS, false)
        tmpStatus = !tmpStatus
        with(pref.edit()) {
            putBoolean(CURRENT_EPISODE_PLAY_STATUS, tmpStatus)
            commit()
        }
    }

    fun isStatusEnabled() = pref.getBoolean(CURRENT_EPISODE_PLAY_STATUS, false)


}