package fr.ffnet.downloader.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.android.support.DaggerFragment
import fr.ffnet.downloader.R
import fr.ffnet.downloader.common.FragmentScope
import kotlinx.android.synthetic.main.fragment_search.*
import javax.inject.Inject

@FragmentScope
class SearchFragment : DaggerFragment(), ChapterListAdapter.ChapterClickListener {

    @Inject lateinit var viewModel: SearchViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false).also {
            activity?.title = resources.getString(R.string.search_fanfiction_title)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        fetchInformationButton.setOnClickListener {
            widgetVisibilityGroup.visibility = View.GONE
            viewModel.loadFanfictionInfos(downloadUrlEditText.text.toString())
        }

        downloadButton.setOnClickListener {
            viewModel.loadChapters()
            viewModel.getChapterList().observe(this, Observer { chapterList ->
                (chapterListRecyclerView.adapter as ChapterListAdapter).chapterList = chapterList
            })
        }
        initRecyclerView()

        viewModel.getCurrentFanfiction().observe(this, Observer {
            widgetVisibilityGroup.visibility = View.VISIBLE
            titleValueTextView.text = it.title
            wordsValueTextView.text = it.words
            publishedDateValueTextView.text = it.publishedDate
            updatedDateValueTextView.text = it.updatedDate
            (chapterListRecyclerView.adapter as ChapterListAdapter).chapterList = it.chapterList

            viewModel.getChapterSyncingProgression().observe(this, Observer { chapterProgression ->
                chaptersValueTextView.text = chapterProgression
            })
        })
    }

    override fun onChapterSelected(chapter: ChapterViewModel) {
        Toast.makeText(context, "Chapter selected ${chapter.title}", Toast.LENGTH_LONG).show()
    }

    private fun initRecyclerView() {
        chapterListRecyclerView.layoutManager = LinearLayoutManager(context)
        chapterListRecyclerView.adapter = ChapterListAdapter(this)
    }
}
