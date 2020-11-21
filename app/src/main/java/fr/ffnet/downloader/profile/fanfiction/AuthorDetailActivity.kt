package fr.ffnet.downloader.profile.fanfiction

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import fr.ffnet.downloader.R
import kotlinx.android.synthetic.main.activity_author_detail.*
import javax.inject.Inject

class AuthorDetailActivity : AppCompatActivity() {

    @Inject lateinit var viewModel: AuthorDetailViewModel

    companion object {

        private const val EXTRA_AUTHOR_ID = "EXTRA_AUTHOR_ID"
        private const val EXTRA_AUTHOR_NAME = "EXTRA_AUTHOR_NAME"
        private const val EXTRA_STORIES_FIRST = "EXTRA_STORIES_FIRST"

        fun newIntent(
            context: Context,
            authorId: String,
            authorName: String,
            shouldShowStoriesFirst: Boolean
        ): Intent = Intent(
            context, AuthorDetailActivity::class.java
        ).apply {
            putExtra(EXTRA_AUTHOR_ID, authorId)
            putExtra(EXTRA_AUTHOR_NAME, authorName)
            putExtra(EXTRA_STORIES_FIRST, shouldShowStoriesFirst)
        }
    }

    private val shouldShowStoriesFirst by lazy {
        intent.getBooleanExtra(EXTRA_STORIES_FIRST, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_author_detail)

        toolbar.title = intent.getStringExtra(EXTRA_AUTHOR_NAME)
        setSupportActionBar(toolbar)
        initTabLayout()
    }

    private fun initTabLayout() {
        fanfictionsViewPager.adapter = FanfictionsTabAdapter(supportFragmentManager).apply {

            val storiesFragment = resources.getString(R.string.profile_my_stories) to ProfileFanfictionFragment.newInstance(
                isFavorites = false,
                authorId = intent.getStringExtra(EXTRA_AUTHOR_ID) ?: ""
            )
            val favoritesFragment = resources.getString(R.string.profile_my_favorites) to ProfileFanfictionFragment.newInstance(
                isFavorites = true,
                authorId = intent.getStringExtra(EXTRA_AUTHOR_ID) ?: ""
            )

            fragmentList = if (shouldShowStoriesFirst) {
                listOf(storiesFragment, favoritesFragment)
            } else {
                listOf(favoritesFragment, storiesFragment)
            }
        }
        profileFanfictionsTabLayout.setupWithViewPager(fanfictionsViewPager)
    }
}
