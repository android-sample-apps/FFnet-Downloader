package fr.ffnet.downloader.fanfiction.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import dagger.android.support.DaggerFragment
import fr.ffnet.downloader.R
import fr.ffnet.downloader.fanfiction.FanfictionActivity.Companion.EXTRA_FANFICTION_ID
import kotlinx.android.synthetic.main.fragment_fanfiction_info.*
import javax.inject.Inject

class FanfictionInfoFragment : DaggerFragment() {

    @Inject lateinit var viewModel: FanfictionInfoViewModel

    companion object {
        fun newInstance(fanfictionId: String): FanfictionInfoFragment =
            FanfictionInfoFragment().apply {
                arguments = Bundle().apply {
                    putString(EXTRA_FANFICTION_ID, fanfictionId)
                }
            }
    }

    private val fanfictionId by lazy { arguments?.getString(EXTRA_FANFICTION_ID) ?: "" }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_fanfiction_info, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loadFanfictionInfo(fanfictionId)

        setListeners(fanfictionId)
        setObservers()
    }

    private fun setObservers() {
        viewModel.getFanfictionInfo().observe(this, Observer {
            widgetVisibilityGroup.visibility = View.VISIBLE
            titleValueTextView.text = it.fanfictionTitleAndAuthor
            wordsValueTextView.text = it.words
            publishedDateValueTextView.text = it.publishedDate
            updatedDateValueTextView.text = it.updatedDate
            syncedDateValueTextView.text = it.syncedDate
            chaptersValueTextView.text = it.progressionText
            Glide.with(requireContext())
                .load(it.imageUrl)
                .into(fanfictionImageView)
        })

        viewModel.getDownloadButtonState()
            .observe(viewLifecycleOwner, Observer { (buttonText, shoudEnabled) ->
                epubButton.isEnabled = shoudEnabled
            })
    }

    private fun setListeners(fanfictionId: String) {
        epubButton.setOnClickListener {
            viewModel.syncChapters(fanfictionId)
        }
    }
}
