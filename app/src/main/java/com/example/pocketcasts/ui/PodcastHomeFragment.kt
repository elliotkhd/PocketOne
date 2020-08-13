package com.example.pocketcasts.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pocketcasts.R
//import com.codingending.popuplayout.PopupLayout
import com.example.pocketcasts.adapter.HomeAdapter
import com.example.pocketcasts.data.MyDatabase
import kotlinx.android.synthetic.main.podcast_home_fragment.*


class PodcastHomeFragment : Fragment() {
    //    private val viewModel by viewModels<PodcastsGridAndHomeViewModel>()
    private val viewModel by viewModels<PodcastHomeViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.podcast_home_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val podcastId = arguments?.getLong("PodcastId")
        val podcastWithEpisodes =
            podcastId?.let {
                MyDatabase.getInstance().podcastDao()
                    .getPodcastWithEpisodes(it)
            }
        val homeAdapterTmp =
            podcastWithEpisodes?.let { HomeAdapter(it, requireActivity()) }
        PodcastHomeRecycler.apply {
            adapter = homeAdapterTmp
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
    }
}