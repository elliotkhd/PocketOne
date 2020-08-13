package com.example.pocketcasts.data

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.*

@Entity(indices = [Index(value = ["sourceUrl"], unique = true)])
data class Podcast(
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null,
    var sourceUrl: String? = null,
    var link: String? = null,
    var title: String? = null,
    var subtitle: String? = null,
    var description: String? = null,
    var encoded: String? = null,
    var lastBuildDate: Date? = null,
    var cover: String? = null,
    @Ignore
    var episodes: MutableList<Episode> = mutableListOf(),
    var author: String? = null,
    var email: String? = null,
    var summary: String? = null,
    var explicit: String? = null,
    var addDate: Long? = null
)