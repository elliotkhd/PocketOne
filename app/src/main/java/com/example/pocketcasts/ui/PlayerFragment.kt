package com.example.pocketcasts.ui

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.pocketcasts.R
import com.example.pocketcasts.service.MusicService


import kotlinx.android.synthetic.main.player_fragment.*


class PlayerFragment : Fragment() {

    private val viewModel by viewModels<PlayerViewModel>()
    private var musicController: MusicService.MusicController? = null
    private var mBound = false
    private val intent = Intent()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.player_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val url = arguments?.get("EPISODE_URL")
        playButton.setOnClickListener {
//            (activity as MainActivity).playButtonClicked()
        }

    }

    fun myUnbind(isUnbind:Boolean) {
        if (!isUnbind) {
//            musicController!!.pause()
            requireActivity().unbindService(connection)
            requireActivity().stopService(intent)
        }
    }

    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            musicController = service as MusicService.MusicController

            mBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }

}