package com.example.pocketcasts.data

import androidx.paging.DataSource
import androidx.paging.PositionalDataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface EpisodeDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(episode: Episode): Long

    @Query("select * from Episode")
    fun queryAllEpisodes(): MutableList<Episode>

    @Query("select * from Episode where podcastId = :podcastId")
    fun queryByPodcastId(podcastId: Long): List<Episode>

    @Query("select * from Episode where id = :id")
    fun findEpisodeById(id: Long): Episode?

    @Query("update Episode set progress = :progress where id = :id")
    fun updateProgress(id: Long, progress: Int)

    @Query("select * from Episode order by timestamp desc")
    fun getAllEpisodeByTimestamp(): DataSource.Factory<Int, Episode>
}