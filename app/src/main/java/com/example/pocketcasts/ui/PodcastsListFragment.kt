package com.example.pocketcasts.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.pocketcasts.R

class PodcastsListFragment : Fragment() {

    companion object {
        fun newInstance() = PodcastsListFragment()
    }

    private lateinit var viewModel: PodcastsListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.podcasts_list_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(PodcastsListViewModel::class.java)
        // TODO: Use the ViewModel
    }

}