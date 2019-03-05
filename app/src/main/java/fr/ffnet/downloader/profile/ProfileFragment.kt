package fr.ffnet.downloader.profile

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import dagger.android.support.DaggerFragment
import fr.ffnet.downloader.R
import fr.ffnet.downloader.common.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_profile.*
import javax.inject.Inject

class ProfileFragment : DaggerFragment() {

    private lateinit var viewModel: ProfileViewModel
    @Inject lateinit var viewModelFactory: ViewModelFactory<ProfileViewModel>

    companion object {
        private const val DISPLAY_ASSOCIATE = 0
        private const val DISPLAY_LIST = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_profile, container, false).also {
        activity?.title = resources.getString(R.string.profile_title)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ProfileViewModel::class.java)
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
        }
        return true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initTabLayout()
        fetchInformationButton.setOnClickListener {
            viewModel.associateProfile(profileUrlEditText.text.toString())
        }
        viewModel.loadIsProfileAssociated()
        viewModel.getIsAssociated().observe(this, Observer { isAssociated ->
            if (isAssociated) {
                setHasOptionsMenu(true)
                profileAssociationStatusViewFlipper.displayedChild = DISPLAY_LIST
                noFanfictionFoundTextView.visibility = View.VISIBLE
            } else {
                setHasOptionsMenu(false)
                profileAssociationStatusViewFlipper.displayedChild = DISPLAY_ASSOCIATE
                noFanfictionFoundTextView.visibility = View.GONE
            }
        })
    }

    private fun dissociateProfile() {
        activity?.let {
            AlertDialog.Builder(it).apply {
                setPositiveButton(R.string.ok) { _, _ ->
                    viewModel.dissociateProfile()
                }
                setNegativeButton(R.string.cancel) { dialog, id ->
                    dialog.cancel()
                }
                setMessage(R.string.profile_dissociate_confirmation)
            }.show()
        }
    }

    private fun initTabLayout() {
        fanfictionsViewPager.adapter = FanfictionsTabAdapter(
            activity!!.supportFragmentManager
        ).apply {
            fragmentList = listOf(
                resources.getString(R.string.profile_my_favorites) to ProfileFanfictionFragment.newInstance(true),
                resources.getString(R.string.profile_my_stories) to ProfileFanfictionFragment.newInstance(false)
            )
        }
        fanfictionsTabLayout.setupWithViewPager(fanfictionsViewPager)
    }
}
