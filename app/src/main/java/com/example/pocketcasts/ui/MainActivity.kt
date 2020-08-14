package com.example.pocketcasts.ui

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.text.InputType
import android.view.Menu
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.bumptech.glide.Glide
import com.example.pocketcasts.R
import com.example.pocketcasts.data.Episode
import com.example.pocketcasts.data.MyDatabase
import com.example.pocketcasts.service.MusicService
import com.example.pocketcasts.util.RssParser
import com.example.pocketcasts.util.SPUtil
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    lateinit var musicController: MusicService.MusicController
    private var mBound = false
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            musicController = service as MusicService.MusicController
            mBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (SPUtil.getInstance().getEpisodeId() == (-10).toLong())
            SPUtil.getInstance().saveEpisodeId(-1)
        SPUtil.getInstance().disablePlaying()
        Intent(this, MusicService::class.java).apply {
            bindService(this, connection, Context.BIND_AUTO_CREATE)
        }
        val navController = findNavController(R.id.fragment)
        val appBarConfiguration = AppBarConfiguration.Builder(bottomNavigationView.menu).build()
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)
        NavigationUI.setupWithNavController(bottomNavigationView, navController)
        if (getCurrentEpisode() != null) {
            val pod = getCurrentEpisode()?.podcastId?.let {
                MyDatabase.getInstance().podcastDao().getPodcastById(it)
            }
            Glide.with(this).asBitmap().load(pod?.cover).into(playBarImage)
        }
        mainPlayButton.setOnClickListener {
            val tmpEpisode = getCurrentEpisode()
            if (tmpEpisode != null) playButtonClicked(tmpEpisode)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)
        val menuItem = menu.findItem(R.id.search)
        val searchView = menuItem.actionView as SearchView
        searchView.apply {
            isIconified = false
            isIconifiedByDefault = false
            isSubmitButtonEnabled = true
            inputType = InputType.TYPE_TEXT_VARIATION_URI

            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(newText: String?): Boolean {
                    val rssParser = RssParser()
                    GlobalScope.launch {
                        withContext(Dispatchers.IO) {
                            rssParser.parseXml(newText, applicationContext)
                        }
                        withContext(Dispatchers.Main) {
                            Toast.makeText(applicationContext, newText, Toast.LENGTH_SHORT).show()
                        }
                    }
                    searchView.clearFocus()
                    searchView.onActionViewCollapsed()
                    return false
                }

                override fun onQueryTextChange(query: String?): Boolean {
                    return false
                }
            })
        }
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val controller = Navigation.findNavController(
            this,
            R.id.fragment
        )
        return controller.navigateUp()
    }

    override fun onDestroy() {
        unbindService(connection)
        SPUtil.getInstance().disablePlaying()
        super.onDestroy()
    }

    private fun playMusic(episode: Episode) {
        if (SPUtil.getInstance().isStatusEnabled()) {
            musicController.play(episode)
        } else {
            musicController.pause()
        }
    }

    fun playButtonClicked(episode: Episode) {
        if (episode.audioUrl == getCurrentEpisode()?.audioUrl) {
            SPUtil.getInstance().changeEpisodeStatus()
        } else {
            val newPod =
                episode.podcastId?.let { MyDatabase.getInstance().podcastDao().getPodcastById(it) }
            Glide.with(this).asBitmap().load(newPod?.cover).into(playBarImage)
            SPUtil.getInstance().enablePlaying()
        }
        playMusic(episode)
        if (SPUtil.getInstance().isStatusEnabled())
            mainPlayButton.setMinAndMaxProgress(0f, 0.5f)
        else
            mainPlayButton.setMinAndMaxProgress(0.5f, 1f)
        mainPlayButton.speed = 2f
        mainPlayButton.playAnimation()
    }

    fun getCurrentEpisode(): Episode? {
        val tmpId = SPUtil.getInstance().getEpisodeId()
        return MyDatabase.getInstance().episodeDao()
            .findEpisodeById(tmpId)
    }
}