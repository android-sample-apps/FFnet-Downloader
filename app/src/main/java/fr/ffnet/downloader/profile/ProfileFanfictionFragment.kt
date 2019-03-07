package fr.ffnet.downloader.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.android.support.DaggerFragment
import fr.ffnet.downloader.R
import fr.ffnet.downloader.common.ViewModelFactory
import fr.ffnet.downloader.fanfiction.FanfictionActivity
import fr.ffnet.downloader.profile.ProfileViewModel.ProfileFanfictionsResult
import fr.ffnet.downloader.synced.FanfictionSyncedUIModel
import fr.ffnet.downloader.utils.FanfictionAction
import fr.ffnet.downloader.utils.OnActionsClickListener
import kotlinx.android.synthetic.main.fragment_profile_fanfictions.*
import javax.inject.Inject

class ProfileFanfictionFragment : DaggerFragment(), OnActionsClickListener {

    companion object {
        private const val DISPLAY_NO_FANFICTIONS = 0
        private const val DISPLAY_LIST = 1
        private const val EXTRA_IS_FAVORITE = "EXTRA_IS_FAVORITE"

        fun newInstance(isFavorites: Boolean): ProfileFanfictionFragment {
            return ProfileFanfictionFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(EXTRA_IS_FAVORITE, isFavorites)
                }
            }
        }
    }

    private lateinit var viewModel: ProfileViewModel
    @Inject lateinit var viewModelFactory: ViewModelFactory<ProfileViewModel>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_profile_fanfictions, container, false).also {
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ProfileViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()
        viewModel.loadFavoriteFanfictions()
        viewModel.loadMyFanfictions()

        arguments?.getBoolean(EXTRA_IS_FAVORITE, false)?.let { isFavorites ->
            if (isFavorites) {
                viewModel.getMyFavoritesList().observe(this, Observer {
                    onProfileFanfictionsResult(it)
                })
            } else {
                viewModel.getMyStoriesResult().observe(this, Observer {
                    onProfileFanfictionsResult(it)
                })
            }
        }

        viewModel.navigateToFanfiction.observe(this, Observer { liveEvent ->
            liveEvent.getContentIfNotHandled()?.let {
                if (context != null) {
                    progressBar.visibility = View.GONE
                    startActivity(FanfictionActivity.intent(context!!, it))
                }
            }
        })
    }

    private fun onProfileFanfictionsResult(result: ProfileViewModel.ProfileFanfictionsResult) {
        when (result) {
            is ProfileFanfictionsResult.ProfileHasFanfictions -> {
                showFanfictions(result.fanfictionList)
            }
            is ProfileFanfictionsResult.ProfileHasNoFanfictions -> {
                profileFanfictionsViewFlipper.displayedChild = DISPLAY_NO_FANFICTIONS
            }
        }
    }

    private fun showFanfictions(fanfictionList: List<FanfictionSyncedUIModel>) {
        (fanfictionRecyclerView.adapter as MyFanfictionsAdapter).fanfictionList = fanfictionList
        profileFanfictionsViewFlipper.displayedChild = DISPLAY_LIST
    }

    override fun onActionClicked(fanfictionId: String, action: FanfictionAction) {
        when (action) {
            FanfictionAction.GOTO_FANFICTION -> fetchFanfictionInformation(fanfictionId)
            FanfictionAction.EXPORT_PDF -> Log.d("ACTION", "EXPORT_PDF")
            FanfictionAction.EXPORT_EPUB -> Log.d("ACTION", "EXPORT_EPUB")
            FanfictionAction.DELETE_FANFICTION -> TODO()
        }
    }

    private fun initRecyclerView() {
        fanfictionRecyclerView.layoutManager = LinearLayoutManager(context)
        fanfictionRecyclerView.adapter = MyFanfictionsAdapter(this)
    }

    private fun fetchFanfictionInformation(fanfictionId: String) {
        progressBar.visibility = View.VISIBLE
        viewModel.loadFanfictionInfo(fanfictionId)
    }
}
