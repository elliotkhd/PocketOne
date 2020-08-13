package com.example.pocketcasts.util

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.example.pocketcasts.data.*
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import java.io.IOException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class RssParser {
    private lateinit var podcast: Podcast
    private lateinit var currentEpisode: Episode
    private val episodes = mutableListOf<Episode>()
    private var insideItem = false
    private var insideChannel = false
    private var insideImage = false
    private val formatter: DateFormat =
        SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.US)
    private lateinit var podcastDao: PodcastDao
    private lateinit var episodeDao: EpisodeDao

    fun parseXml(url: String?, context: Context) {
        val stringRequest = EncodedStringRequest(
            Request.Method.GET, url,
            Response.Listener<String> { response ->
                if (url != null) parseXmlWithString(response, url)

            },
            Response.ErrorListener { Log.d("some tag", "request failed") })
        VolleyUtil.getInstance(context).addToRequestQueue(stringRequest)
    }

    @Throws(XmlPullParserException::class, IOException::class)
    fun parseXmlWithString(response: String, url: String) {
        podcastDao = MyDatabase.getInstance().podcastDao()
        episodeDao = MyDatabase.getInstance().episodeDao()
        val factory = XmlPullParserFactory.newInstance()
        factory.isNamespaceAware = true
        val parser = factory.newPullParser()
        parser.setInput(response.byteInputStream(), "UTF_8")
        var eventType: Int = parser.eventType
        loop@ while (eventType != XmlPullParser.END_DOCUMENT) {
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    when (parser.name) {
                        "channel" -> {
                            podcast = Podcast()
                            insideChannel = true
                            if (podcastDao.isPodcastSourceUrlExists(url) != null) {
                                println(
                                    "already existed" + podcastDao.isPodcastSourceUrlExists(url)
                                )
                                return
                            } else podcast.sourceUrl = url
                        }
                        "item" -> {
                            currentEpisode = Episode()
                            insideItem = true
                        }
                        "title" -> if (!insideImage) {
                            when {
                                insideItem -> currentEpisode.title = parser.nextText()
                                insideChannel -> podcast.title = parser.nextText()
                            }
                        }
                        "duration" -> {
                            val tmp = parser.nextText()
                            val regex = Regex("[A-Za-z]")
                            val tmpA = tmp.replace(regex, "")
                            val tmpStrings = tmpA.split(":")
                            println("tmpStrings = $tmpStrings")
                            when (tmpStrings.size) {
                                1 -> currentEpisode.duration = tmpStrings[0].toFloat().toInt()
                                2 -> currentEpisode.duration =
                                    tmpStrings[0].toInt().times(60) + tmpStrings[1].toInt()
                                3 -> currentEpisode.duration =
                                    tmpStrings[0].toInt().times(3600) + tmpStrings[1].toInt()
                                        .times(60) + tmpStrings[2].toInt()
                                else -> currentEpisode.duration = 0
                            }
                        }
                        "link" ->
                            if (parser.namespace.isEmpty()) {
                                when {
                                    insideItem -> currentEpisode.link = parser.nextText()
                                    insideChannel -> podcast.link = parser.nextText()

                                }
                            }
                        "description" -> when {
                            insideItem -> currentEpisode.description = parser.nextText()
                            insideChannel -> podcast.description = parser.nextText()
                        }
                        "lastBuildDate" -> podcast.lastBuildDate = formatDate(parser.nextText())
                        "pubDate" -> when {
                            insideItem -> {
                                val tmp = formatDate(parser.nextText())
                                currentEpisode.pubDate = tmp
                                currentEpisode.timestamp = tmp.time
                            }
                            insideChannel -> podcast.lastBuildDate =
                                formatDate(parser.nextText())
                        }
                        "subtitle" -> when {
                            insideItem -> currentEpisode.subtitle = parser.nextText()
                            insideChannel -> podcast.subtitle = parser.nextText()
                        }
                        "encoded" -> when {
                            insideItem -> currentEpisode.encoded = parser.nextText()
                            insideChannel -> podcast.encoded = parser.nextText()
                        }
                        "author" -> when {
                            insideItem -> currentEpisode.author = parser.nextText()
                            insideChannel -> podcast.author = parser.nextText()
                        }
                        "email" -> podcast.email = parser.nextText()
                        "summary" -> when {
                            insideItem -> currentEpisode.description = parser.nextText()
                            insideChannel -> podcast.summary = parser.nextText()
                        }
                        "explicit" -> when {
                            insideItem -> currentEpisode.explicit = parser.nextText()
                            insideChannel -> podcast.explicit = parser.nextText()
                        }
                        "image" -> {
                            insideImage = true
                            if (parser.namespace.isNotEmpty()) {
                                when {
                                    insideItem ->
                                        currentEpisode.episodeCoverUrl = parser.getAttributeValue(0)
                                    insideChannel -> podcast.cover = parser.getAttributeValue(0)
                                }
                            }
                        }
                        "enclosure" -> {
                            currentEpisode.audioUrl =
                                parser.getAttributeValue(null, "url")
                            val mediaLength =
                                parser.getAttributeValue(null, "length").toLongOrNull() ?: this
                            val tmp: String
                            tmp = if (mediaLength is Long) {
                                mediaLength.div(1048576).toString() + " MB"
                            } else
                                mediaLength.toString()
                            currentEpisode.length = tmp
                        }

                    }
                }
                XmlPullParser.END_TAG -> {
                    when (parser.name) {
                        "item" -> {
                            insideItem = false
                            episodes.add(currentEpisode)
                        }
                        "channel" -> {
                            insideChannel = false
                            podcast.addDate = Date().time
                            val podcastId = podcastDao.insert(podcast)
                            episodes.forEach {
                                it.podcastId = podcastId
                                episodeDao.insert(it)
                            }
                        }
                        "image" -> insideImage = false
                    }
                }
            }
            eventType = parser.next()
        }
        println("aaaEnd document")
    }

    private fun formatDate(dateString: String): Date {
        return formatter.parse(dateString) ?: Date()
    }
}