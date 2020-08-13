package com.example.pocketcasts.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.pocketcasts.data.*

//shared viewModel for PodcastGridFragment and PodcastHomeFragment
class PodcastsGridViewModel(application: Application) : AndroidViewModel(application) {
//    val podcasts: List<Podcast> = mutableListOf()
    private val database = MyDatabase.getInstance()
    fun getPodcastsFromDatabase(): LiveData<List<Podcast>> {
        return database.podcastDao().queryAll()
    }
}