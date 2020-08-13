package com.example.pocketcasts.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.paging.Config
import androidx.paging.toLiveData
import com.example.pocketcasts.data.MyDatabase

class FiltersViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = MyDatabase.getInstance().episodeDao()

    companion object {
        private const val PAGE_SIZE = 60
        private const val ENABLE_PLACEHOLDERS = true
    }

    val getAllEpisode = dao.getAllEpisodeByTimestamp().toLiveData(
        Config(
            pageSize = PAGE_SIZE,
            enablePlaceholders = ENABLE_PLACEHOLDERS,
            initialLoadSizeHint = PAGE_SIZE
        )
    )

}