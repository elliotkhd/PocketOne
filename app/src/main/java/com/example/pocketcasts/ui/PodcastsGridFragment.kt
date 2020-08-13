package com.example.pocketcasts.ui

import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.example.pocketcasts.R
import com.example.pocketcasts.adapter.GridAdapter
import kotlinx.android.synthetic.main.podcasts_grid_fragment.*

class PodcastsGridFragment : Fragment() {

    private val viewModel by viewModels<PodcastsGridViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.podcasts_grid_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val podcasts = viewModel.getPodcastsFromDatabase()
        val wm = requireContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val dm = DisplayMetrics()
        wm.defaultDisplay.getMetrics(dm)
        val width = dm.widthPixels // 屏幕宽度（像素）
        val mAdapter = GridAdapter(width)

        gridRecyclerView.apply {
            adapter = mAdapter
            layoutManager =
                GridLayoutManager(
                    requireContext(), 4,
                    GridLayoutManager.VERTICAL, false
                )
        }
        podcasts.observe(viewLifecycleOwner,
            Observer {
                mAdapter.submitList(it)
//                println(podcasts)
            })
    }

}