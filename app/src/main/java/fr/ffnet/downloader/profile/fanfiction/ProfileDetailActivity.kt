package fr.ffnet.downloader.profile.fanfiction

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import fr.ffnet.downloader.R
import kotlinx.android.synthetic.main.activity_author_detail.*

class ProfileDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_author_detail)

        setSupportActionBar(toolbar)
        initTabLayout()
    }

    private fun initTabLayout() {
        fanfictionsViewPager.adapter = FanfictionsTabAdapter(supportFragmentManager).apply {
            fragmentList = listOf(
                resources.getString(
                    R.string.profile_my_favorites
                ) to ProfileFanfictionFragment.newInstance(true),
                resources.getString(
                    R.string.profile_my_stories
                ) to ProfileFanfictionFragment.newInstance(false)
            )
        }
        profileFanfictionsTabLayout.setupWithViewPager(fanfictionsViewPager)
    }
}
