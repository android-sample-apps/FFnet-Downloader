package fr.ffnet.downloader.profile

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.lifecycle.Observer
import dagger.android.support.DaggerFragment
import fr.ffnet.downloader.R
import fr.ffnet.downloader.fanfiction.FanfictionActivity
import fr.ffnet.downloader.synced.FanfictionSyncedUIModel
import fr.ffnet.downloader.utils.FanfictionAction
import fr.ffnet.downloader.utils.OnActionsClickListener
import kotlinx.android.synthetic.main.fragment_profile.*
import javax.inject.Inject

class ProfileFragment : DaggerFragment(), OnActionsClickListener {

    @Inject lateinit var viewModel: ProfileViewModel

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

        initViewPager()
        fetchInformationButton.setOnClickListener {
            viewModel.associateProfile(profileUrlEditText.text.toString())
        }

        viewModel.loadIsProfileAssociated()
        viewModel.loadFanfictionsFromProfile()

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
        viewModel.getFanfictionList().observe(this, Observer {
            when (it) {
                is ProfileViewModel.ProfileFanfictionsResult.ProfileHasFanfictions -> {
                    showFanfictions(it.fanfictionList)
                }
                is ProfileViewModel.ProfileFanfictionsResult.ProfileHasNoFanfictions -> {
                    profileAssociationStatusViewFlipper.displayedChild = DISPLAY_ASSOCIATE
                }
            }
        })

        viewModel.navigateToFanfiction.observe(this, Observer { liveEvent ->
            liveEvent.getContentIfNotHandled()?.let {
                if (context != null) {
                    startActivity(FanfictionActivity.intent(context!!, it))
                }
            }
        })
    }

    override fun onActionClicked(fanfictionId: String, action: FanfictionAction) {
        when (action) {
            FanfictionAction.GOTO_FANFICTION -> fetchFanfictionInformation(
                fanfictionId
            )
            FanfictionAction.EXPORT_PDF -> Log.d("ACTION", "EXPORT_PDF")
            FanfictionAction.EXPORT_EPUB -> Log.d("ACTION", "EXPORT_EPUB")
            FanfictionAction.DELETE_FANFICTION -> {

            }
        }
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

    private fun fetchFanfictionInformation(fanfictionId: String) {
        viewModel.loadFanfictionInfo(fanfictionId)
    }

    private fun showFanfictions(fanfictionList: List<FanfictionSyncedUIModel>) {
        (fanfictionsViewPager.adapter as FanfictionViewPagerAdapter).adapterList = listOf(
            fanfictionList to MyFanfictionsAdapter(this),
            fanfictionList to MyFanfictionsAdapter(this)
        )
        profileAssociationStatusViewFlipper.displayedChild = DISPLAY_LIST
    }

    private fun initViewPager() {
        fanfictionsViewPager.adapter = FanfictionViewPagerAdapter()
    }
}
