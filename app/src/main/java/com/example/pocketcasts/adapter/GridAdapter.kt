package com.example.pocketcasts.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.pocketcasts.R
import com.example.pocketcasts.data.Podcast
import kotlinx.android.synthetic.main.podcast_grid_item.view.*

class GridAdapter(val width: Int) : ListAdapter<Podcast, GridAdapter.VH>(DIFFCALLBACK) {
    class VH(private val item: View) : RecyclerView.ViewHolder(item)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val item =
            LayoutInflater.from(parent.context).inflate(R.layout.podcast_grid_item, parent, false)
        return VH(item)
    }


    override fun onBindViewHolder(holder: VH, position: Int) {
//        holder.itemView.podcastGridItemName.text = getItem(position).title


        Glide.with(holder.itemView)
            .asBitmap()
            .load(getItem(position).cover)
            .apply(RequestOptions().override(width / 4))
            .into(holder.itemView.podcastGridItemImage)

        holder.itemView.setOnClickListener {
            Bundle().apply {
                putLong("PodcastId", getItem(position).id ?: 0)
                holder.itemView.findNavController()
                    .navigate(R.id.action_podcastsGridFragment_to_podcastHomeFragment, this)
            }
        }
    }

    object DIFFCALLBACK : DiffUtil.ItemCallback<Podcast>() {
        override fun areItemsTheSame(oldItem: Podcast, newItem: Podcast): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Podcast, newItem: Podcast): Boolean {
            return oldItem.id == newItem.id
        }

    }
}