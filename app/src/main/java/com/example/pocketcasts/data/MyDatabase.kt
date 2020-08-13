package com.example.pocketcasts.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.pocketcasts.util.App

@Database(entities = [Podcast::class, Episode::class], version = 1, exportSchema = false)
@TypeConverters(Converter::class)
abstract class MyDatabase : RoomDatabase() {
    abstract fun podcastDao(): PodcastDao
    abstract fun episodeDao(): EpisodeDao

    companion object {

        // For Singleton instantiation
        @Volatile
        private var instance: MyDatabase? = null

        fun getInstance(): MyDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(App.instance).also { instance = it }
            }
        }

        // Create and pre-populate the database. See this article for more details:
        // https://medium.com/google-developers/7-pro-tips-for-room-fbadea4bfbd1#4785
        private fun buildDatabase(context: Context): MyDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                MyDatabase::class.java,
                "podcast_database"
            )
                .allowMainThreadQueries()
                .build()
        }
    }

}