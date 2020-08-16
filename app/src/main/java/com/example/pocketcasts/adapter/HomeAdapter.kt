package com.example.pocketcasts.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.pocketcasts.R
import com.example.pocketcasts.data.PodcastWithEpisodes
import com.example.pocketcasts.ui.EpisodeDetailFragment
import com.example.pocketcasts.ui.MainActivity
import com.example.pocketcasts.util.SPUtil
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.podcast_home_header.view.*
import kotlinx.android.synthetic.main.podcast_home_normal.view.*
import java.text.SimpleDateFormat
import java.util.*

class HomeAdapter(
    private val pWithe: PodcastWithEpisodes,
    private val context: FragmentActivity
) :
    RecyclerView.Adapter<HomeAdapter.VH>() {
    companion object {
        const val NORMAL_VIEW_TYPE = 0
        const val HEAD_VIEW_TYPE = 1
    }

    private var currentPlayedItem: Int = -1
    private var currentPlayedEpisodeStatus = false

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        println("FilterAdapter.onAttachedToRecyclerView")
        (context as MainActivity).apply {
            mainPlayButton.setOnClickListener {
                val tmpEpisode = getCurrentEpisode()
                if (tmpEpisode != null) playButtonClicked(tmpEpisode)
                notifyItemChanged(currentPlayedItem)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        currentPlayedItem = getCurrentPlayedItem()
        pWithe.episodes.sortByDescending { it.timestamp }

        return if (viewType == NORMAL_VIEW_TYPE) {
            VH(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.podcast_home_normal, parent, false)
            )
        } else {
            VH(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.podcast_home_header, parent, false)
            )
        }
    }

    override fun getItemCount(): Int = pWithe.episodes.size + 1

    override fun onBindViewHolder(holder: VH, position: Int) {
        if (position == 0) {
            holder.itemView.apply {
                pHomeAuthor.text = pWithe.podcast?.author
                pHomeDescription.text = pWithe.podcast?.description
                pHomeTitle.text = pWithe.podcast?.title ?: "title not exists"
                Glide.with(this)
                    .load(pWithe.podcast?.cover)
                    .apply(RequestOptions().override(300, 300))
                    .apply(RequestOptions().transform(RoundedCorners(15)))
                    .placeholder(R.drawable.photo_place_holder)
                    .into(pHomeCover)
            }
        } else {
            holder.itemView.homePlayButton.apply {
                currentPlayedEpisodeStatus = SPUtil.getInstance().isStatusEnabled()
                if (position == currentPlayedItem && currentPlayedEpisodeStatus)
                    setImageResource(R.drawable.ic_baseline_pause_circle_outline_24)
                else
                    setImageResource(R.drawable.ic_outline_play_circle_outline_24)
            }
            holder.itemView.homeTitle.text =
                pWithe.episodes[position - 1].title ?: "title not exists"
            val tmpMin = pWithe.episodes[position - 1].duration?.div(60)
            var tmpString = ""
            tmpString = if (tmpMin?.div(60)!! >= 1)
                tmpMin.div(60).toString() + "h " + tmpMin.rem(60).toString() + "min"
            else tmpMin.rem(60).toString() + "min"
            holder.itemView.homeDuration.text = tmpString
            holder.itemView.homePubdate.text =
                pWithe.episodes[position - 1].pubDate?.let { formatDate(it) }
            holder.itemView.setOnClickListener {
                Bundle().apply {
                    pWithe.episodes[position - 1].id?.let { it1 -> putLong("episodeId", it1) }
                    val episodeDetailFragment = EpisodeDetailFragment.newInstance()
                    episodeDetailFragment.arguments = this
                    episodeDetailFragment.show(
                        context.supportFragmentManager,
                        EpisodeDetailFragment.TAG
                    )
                }
                println("HomeAdapter.onBindViewHolder")
                notifyItemChanged(position)
            }
            holder.itemView.homePlayButton.setOnClickListener {
                playButtonAction(position)
            }
        }
    }


    override fun getItemViewType(position: Int): Int {
        return if (position == 0) HEAD_VIEW_TYPE else NORMAL_VIEW_TYPE
    }

    private fun playButtonAction(position: Int) {
        pWithe.episodes[position - 1].audioUrl?.let {
            (context as MainActivity).playButtonClicked(pWithe.episodes[position - 1])
        }
        if (currentPlayedItem != position) {
            if (currentPlayedItem == -1) {
                currentPlayedItem = position
                notifyItemChanged(currentPlayedItem)
            } else {
                notifyItemChanged(currentPlayedItem)
                currentPlayedItem = position
                notifyItemChanged(currentPlayedItem)
            }
        } else {
            notifyItemChanged(position)
        }

    }

    private fun getCurrentPlayedItem(): Int {
        val currentId = SPUtil.getInstance().getEpisodeId()
        for (i in pWithe.episodes.indices) {
            if (pWithe.episodes[i].id == currentId)
                return i + 1
        }
        return -1
    }

    private fun formatDate(date: Date): String {
        val formatter = SimpleDateFormat("yy/M/d E HH:mm", Locale.CHINA)
        return formatter.format(date)
    }
}