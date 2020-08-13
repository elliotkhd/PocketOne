package com.example.pocketcasts.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface PodcastDao {
    @Transaction
    @Query("select * from Podcast")
    fun queryAll(): LiveData<List<Podcast>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(podcast: Podcast): Long

    @Delete
    fun delete(podcast: Podcast): Int

    @Update
    fun update(vararg podcasts: Podcast): Int

//    @Query("select count(1) from Podcast where sourceUrl = :url")
    @Query("select 1 from Podcast where sourceUrl = :url limit 1")
    fun isPodcastSourceUrlExists(url: String): Int?

    @Transaction
    @Query("select * from Podcast where id = :id")
    fun getPodcastWithEpisodes(id: Long): PodcastWithEpisodes

    @Query("select * from Podcast where id = :id")
    fun getPodcastById(id: Long): Podcast
}