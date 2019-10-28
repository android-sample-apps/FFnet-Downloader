package fr.ffnet.downloader.fanfiction

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import dagger.android.support.DaggerAppCompatActivity
import fr.ffnet.downloader.R
import fr.ffnet.downloader.fanfiction.chapters.FanfictionChaptersFragment
import fr.ffnet.downloader.fanfiction.info.FanfictionInfoFragment
import fr.ffnet.downloader.profile.fanfiction.FanfictionsTabAdapter
import kotlinx.android.synthetic.main.activity_fanfiction.*

class FanfictionActivity : DaggerAppCompatActivity() {

    private val fanfictionId by lazy { intent.getStringExtra(EXTRA_FANFICTION_ID) }
    private val fanfictionTitle by lazy { intent.getStringExtra(EXTRA_FANFICTION_TITLE) }

    companion object {

        const val EXTRA_FANFICTION_ID = "EXTRA_FANFICTION_ID"
        const val EXTRA_FANFICTION_TITLE = "EXTRA_FANFICTION_TITLE"

        fun newIntent(context: Context, fanfictionId: String, fanfictionTitle: String): Intent =
            Intent(
                context, FanfictionActivity::class.java
            ).apply {
                putExtra(EXTRA_FANFICTION_ID, fanfictionId)
                putExtra(EXTRA_FANFICTION_TITLE, fanfictionTitle)
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fanfiction)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.title = fanfictionTitle
        initTabLayout()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initTabLayout() {
        fanfictionViewPager.adapter = FanfictionsTabAdapter(supportFragmentManager).apply {
            fragmentList = listOf(
                resources.getString(
                    R.string.download_info_title
                ) to FanfictionInfoFragment.newInstance(fanfictionId),
                resources.getString(
                    R.string.download_chapters_title
                ) to FanfictionChaptersFragment.newInstance(fanfictionId)
            )
        }
        fanfictionTabLayout.setupWithViewPager(fanfictionViewPager)
    }
}
