package com.example.pocketcasts.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.*


@Entity(indices = [Index(value = ["audioUrl"], unique = true)])
data class Episode(
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null,
    var link: String? = null,
    var podcastId: Long? = null,
    var title: String? = null,
    var timestamp: Long? = null,
    var pubDate: Date? = null,
    var audioUrl: String? = null,
    var episodeCoverUrl: String? = null,
    var subtitle: String? = null,
    var length: String? = null,
    var duration: Int? = null,
    var explicit: String? = null,
    var author: String? = null,
    var description: String? = null,
    var encoded: String? = null,
    var progress: Int = 0
)