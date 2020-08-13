package com.example.pocketcasts.ui

import android.content.res.Resources
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.pocketcasts.R
import com.example.pocketcasts.data.Episode
import com.example.pocketcasts.data.Podcast
import com.example.pocketcasts.data.MyDatabase
import com.example.pocketcasts.util.SPUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.episode_detail_fragment.*
import java.text.SimpleDateFormat
import java.util.*


class EpisodeDetailFragment : BottomSheetDialogFragment() {

    companion object {
        const val TAG = "EpisodeDetailFragment"
        fun newInstance() = EpisodeDetailFragment()
    }

    var episode: Episode? = null
    private var podcast: Podcast? = null
    private var maxProgress = 0.0f
    private lateinit var viewModel: EpisodeDetailViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val episodeId = arguments?.getLong("episodeId")
        episode = episodeId?.let {
            MyDatabase.getInstance().episodeDao()
                .findEpisodeById(
                    it
                )
        }!!
        podcast = episode?.podcastId?.let {
            MyDatabase.getInstance().podcastDao()
                .getPodcastById(
                    it
                )
        }
        return inflater.inflate(R.layout.episode_detail_fragment, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Glide.with(this)
            .asBitmap()
            .load(podcast?.cover)
            .apply(RequestOptions().override(getScreenWidth() / 2))
            .apply(RequestOptions().transform(RoundedCorners(20)))
            .into(detailEpisodeImage)
        detailDate.text = episode?.pubDate?.let { formatDate(it) }
        detailDescription.text = Html.fromHtml(episode?.description, Html.FROM_HTML_MODE_COMPACT)
        detailEpisodeName.text = episode?.title
        detailPodcastName.text = podcast?.title
        detailDuration.text = episode?.duration.toString()
        detailEpisodeImage.scaleType = ImageView.ScaleType.FIT_CENTER
        viewModel = ViewModelProvider(this).get(EpisodeDetailViewModel::class.java)
        detailEpisodeImage.setImageResource(R.drawable.photo_place_holder)
        if (SPUtil.getInstance().isStatusEnabled() && SPUtil.getInstance()
                .getEpisodeId() == episode?.id
        ) detailPlayButton.progress = 0.5f
        else detailPlayButton.progress = 0f
        detailPlayButton.setOnClickListener {
            playButtonClicked()
        }
    }

    private fun getScreenWidth(): Int {
        return Resources.getSystem().displayMetrics.widthPixels
    }

    private fun playButtonClicked() {
        episode?.let { (activity as MainActivity).playButtonClicked(it) }

        if (SPUtil.getInstance().isStatusEnabled()) detailPlayButton.setMinAndMaxProgress(0f, 0.5f)
        else detailPlayButton.setMinAndMaxProgress(0.5f, 1f)
        detailPlayButton.speed = 2f
        detailPlayButton.playAnimation()
    }
    private fun formatDate(date: Date): String {
        val formatter = SimpleDateFormat("yyyy/MM/dd E hh:mm", Locale.CHINA)
        return formatter.format(date)
    }
}