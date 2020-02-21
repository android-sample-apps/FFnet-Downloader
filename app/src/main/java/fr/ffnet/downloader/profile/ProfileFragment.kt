package fr.ffnet.downloader.profile

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import dagger.android.support.DaggerFragment
import fr.ffnet.downloader.R
import fr.ffnet.downloader.profile.fanfiction.FanfictionsTabAdapter
import fr.ffnet.downloader.profile.fanfiction.ProfileFanfictionFragment
import kotlinx.android.synthetic.main.fragment_profile.*
import javax.inject.Inject

class ProfileFragment : DaggerFragment(), ProfileHistoryAdapter.OnHistoryClickListener {

    @Inject lateinit var viewModel: ProfileViewModel

    companion object {
        private const val DISPLAY_ASSOCIATE = 0
        private const val DISPLAY_LIST = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_profile, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        initTabLayout()
        profileHistoryRecyclerView.adapter = ProfileHistoryAdapter(this)
        fetchInformationButton.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            viewModel.associateProfile(profileUrlEditText.text.toString())
        }

        viewModel.loadProfileHistory().observe(viewLifecycleOwner, Observer { historyList ->
            (profileHistoryRecyclerView.adapter as ProfileHistoryAdapter).historyList = historyList
        })

        viewModel.loadIsProfileAssociated()
        viewModel.getIsAssociated().observe(viewLifecycleOwner, Observer { profileName ->
            if (profileName != null) {
                toolbar.title = profileName
                progressBar.visibility = View.GONE
                setHasOptionsMenu(true)
                profileAssociationStatusViewFlipper.displayedChild = DISPLAY_LIST
                profileFanfictionsTabLayout.visibility = View.VISIBLE
                noFanfictionFoundTextView.visibility = View.VISIBLE
            } else {
                toolbar.title = getString(R.string.profile_title)
                setHasOptionsMenu(false)
                profileAssociationStatusViewFlipper.displayedChild = DISPLAY_ASSOCIATE
                profileFanfictionsTabLayout.visibility = View.GONE
                noFanfictionFoundTextView.visibility = View.GONE
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.profile_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.dissociateProfile -> {
                dissociateProfile()
                return true
            }
            R.id.refreshProfile -> {
                viewModel.refreshProfile()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onHistoryClicked(profileId: String, profileUrl: String) {
        profileUrlEditText.setText(profileUrl)
    }

    private fun dissociateProfile() {
        AlertDialog.Builder(requireContext()).apply {
            setPositiveButton(R.string.ok) { _, _ ->
                viewModel.dissociateProfile()
            }
            setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.cancel()
            }
            setMessage(R.string.profile_dissociate_confirmation)
        }.show()
    }

    private fun initTabLayout() {
        fanfictionsViewPager.adapter = FanfictionsTabAdapter(
            requireActivity().supportFragmentManager
        ).apply {
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
