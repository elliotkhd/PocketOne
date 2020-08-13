package com.example.pocketcasts.data

import androidx.room.Embedded
import androidx.room.Relation


class PodcastWithEpisodes(
    @Embedded var podcast: Podcast? = null,
    @Relation(
        parentColumn = "id",
        entityColumn = "podcastId"
    )
    val episodes: MutableList<Episode> = mutableListOf()
)