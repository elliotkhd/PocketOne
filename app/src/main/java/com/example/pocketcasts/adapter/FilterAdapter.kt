package com.example.pocketcasts.adapter

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pocketcasts.R
import com.example.pocketcasts.data.Episode
import com.example.pocketcasts.data.MyDatabase
import com.example.pocketcasts.ui.EpisodeDetailFragment
import com.example.pocketcasts.ui.MainActivity
import com.example.pocketcasts.util.SPUtil
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.paged_item.view.*
import java.text.SimpleDateFormat
import java.util.*

class FilterAdapter(private val context: FragmentActivity) :
    PagedListAdapter<Episode, FilterAdapter.VH>(diffCallback) {

    private var currentPlayedItem: Int = -1
    private val dao = MyDatabase.getInstance().podcastDao()

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
        Log.d("bbbb", "viewholder")

        return VH(
            LayoutInflater.from(parent.context).inflate(R.layout.paged_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        Log.d("aaaa", "position = ${position}")
        holder.itemView.filterPlayButton.apply {
            if (position == currentPlayedItem && SPUtil.getInstance().isStatusEnabled())
                setImageResource(R.drawable.ic_baseline_pause_circle_outline_24)
            else
                setImageResource(R.drawable.ic_outline_play_circle_outline_24)
        }
        holder.itemView.filterTitle.text =
            getItem(position)?.title ?: "title not exists"
        val tmpMin = getItem(position)?.duration?.div(60)
        var tmpString = ""
        tmpString = if (tmpMin?.div(60)!! >= 1)
            tmpMin.div(60).toString() + "h " + tmpMin.rem(60).toString() + "min"
        else tmpMin.rem(60).toString() + "min"
        holder.itemView.filterDuration.text = tmpString
        holder.itemView.filterPubDate.text =
            getItem(position)?.pubDate?.let { formatDate(it) }
        val podcast = getItem(position)?.podcastId?.let { dao.getPodcastById(it) }
        Glide.with(holder.itemView)
            .asBitmap()
            .load(podcast?.cover)
//            .apply(RequestOptions().override(width / 4))
            .into(holder.itemView.filterImage)
        holder.itemView.setOnClickListener {
            Bundle().apply {
                println("position = ${position}")
                getItem(position)?.id?.let { it1 -> putLong("episodeId", it1) }
                val episodeDetailFragment = EpisodeDetailFragment.newInstance()
                episodeDetailFragment.arguments = this
                episodeDetailFragment.show(
                    context.supportFragmentManager,
                    EpisodeDetailFragment.TAG
                )
            }
            notifyItemChanged(position)
        }
        holder.itemView.filterPlayButton.setOnClickListener {
            playButtonAction(position)
        }
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<Episode>() {
            override fun areContentsTheSame(oldItem: Episode, newItem: Episode): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areItemsTheSame(oldItem: Episode, newItem: Episode): Boolean {
                return oldItem == newItem
            }
        }
    }

    private fun playButtonAction(position: Int) {
        getItem(position)?.let { (context as MainActivity).playButtonClicked(it) }
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
        for (i in currentList!!.indices) {
            if (getItem(i)?.id == currentId)
                return i + 1
        }
        return -1
    }

    private fun formatDate(date: Date): String {
        val formatter = SimpleDateFormat("yy/M/d E hh:mm", Locale.CHINA)
        return formatter.format(date)
    }
}
