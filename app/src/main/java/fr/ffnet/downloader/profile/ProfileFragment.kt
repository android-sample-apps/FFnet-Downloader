package fr.ffnet.downloader.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
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
    ): View? = inflater.inflate(R.layout.fragment_profile, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()
        fetchInformationButton.setOnClickListener {
            viewModel.associateProfile(profileUrlEditText.text.toString())
        }

        viewModel.getIsProfileAssociated().observe(this, Observer { isAssociated ->
            if (isAssociated) {
                profileAssociationStatusViewFlipper.displayedChild = DISPLAY_LIST
            } else {
                profileAssociationStatusViewFlipper.displayedChild = DISPLAY_ASSOCIATE
            }
        })

        viewModel.loadFanfictionsFromProfile()
        viewModel.getFanfictionList().observe(this, Observer {
            when (it) {
                is ProfileViewModel.ProfileFanfictionsResult.ProfileHasFanfictions -> {
                    showSyncedFanfictions(it.fanfictionList)
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

    private fun fetchFanfictionInformation(fanfictionId: String) {
        viewModel.loadFanfictionInfo(fanfictionId)
    }

    private fun showSyncedFanfictions(fanfictionList: List<FanfictionSyncedUIModel>) {
        (favoriteFanfictionsRecyclerView.adapter as MyFanfictionsAdapter).fanfictionList = fanfictionList
        profileAssociationStatusViewFlipper.displayedChild = DISPLAY_LIST
    }

    private fun initRecyclerView() {
        favoriteFanfictionsRecyclerView.layoutManager = LinearLayoutManager(context)
        favoriteFanfictionsRecyclerView.adapter = MyFanfictionsAdapter(this)
    }
}
