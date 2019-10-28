package fr.ffnet.downloader.fanfiction.chapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import dagger.android.support.DaggerFragment
import fr.ffnet.downloader.R
import fr.ffnet.downloader.fanfiction.ChapterListAdapter
import fr.ffnet.downloader.fanfiction.ChapterUIModel
import fr.ffnet.downloader.fanfiction.FanfictionActivity.Companion.EXTRA_FANFICTION_ID
import kotlinx.android.synthetic.main.fragment_fanfiction_chapters.*
import javax.inject.Inject

class FanfictionChaptersFragment : DaggerFragment(), ChapterListAdapter.ChapterClickListener {

    @Inject lateinit var viewModel: FanfictionChaptersViewModel

    companion object {
        fun newInstance(fanfictionId: String): FanfictionChaptersFragment =
            FanfictionChaptersFragment().apply {
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
    ): View = inflater.inflate(R.layout.fragment_fanfiction_chapters, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        chapterListRecyclerView.adapter = ChapterListAdapter(this)
        viewModel.loadChapters(fanfictionId)
        setObservers()
    }

    override fun onChapterSelected(chapter: ChapterUIModel) = Unit

    private fun setObservers() {
        viewModel.getChapterList().observe(viewLifecycleOwner, Observer { chapterList ->
            (chapterListRecyclerView.adapter as ChapterListAdapter).chapterList = chapterList
        })
    }
}
