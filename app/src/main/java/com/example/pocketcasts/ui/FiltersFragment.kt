package com.example.pocketcasts.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pocketcasts.R
import com.example.pocketcasts.adapter.FilterAdapter
import kotlinx.android.synthetic.main.filters_fragment.*

class FiltersFragment : Fragment() {

    private val viewModel: FiltersViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.filters_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val filterAdapter = FilterAdapter(requireActivity())
        filterRecycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        filterRecycler.adapter = filterAdapter
        viewModel.getAllEpisode.observe(viewLifecycleOwner, Observer {
            filterAdapter.submitList(it)
        })
    }
}