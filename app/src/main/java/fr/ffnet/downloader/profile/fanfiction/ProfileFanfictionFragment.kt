package fr.ffnet.downloader.profile.fanfiction

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import dagger.android.support.DaggerFragment
import fr.ffnet.downloader.OptionsBottomSheetDialogFragment
import fr.ffnet.downloader.R
import fr.ffnet.downloader.fanfiction.FanfictionActivity
import fr.ffnet.downloader.profile.ProfileViewModel.ProfileFanfictionsResult
import fr.ffnet.downloader.synced.FanfictionSyncedUIModel
import fr.ffnet.downloader.synced.SyncedAdapter
import fr.ffnet.downloader.utils.OnFanfictionOptionsListener
import kotlinx.android.synthetic.main.fragment_profile_fanfictions.*
import javax.inject.Inject

class ProfileFanfictionFragment : DaggerFragment(), OnFanfictionOptionsListener {

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

    private val isFavorites by lazy { arguments?.getBoolean(EXTRA_IS_FAVORITE, false) ?: false }

    @Inject lateinit var viewModel: ProfileFanfictionViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_profile_fanfictions, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fanfictionRecyclerView.adapter = SyncedAdapter(this)
        viewModel.loadFavoriteFanfictions()
        viewModel.loadMyFanfictions()

        if (isFavorites) {
            viewModel.getMyFavoritesList().observe(this, Observer {
                onProfileFanfictionsResult(it)
            })
        } else {
            viewModel.getMyStoriesList().observe(this, Observer {
                onProfileFanfictionsResult(it)
            })
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

    private fun onProfileFanfictionsResult(result: ProfileFanfictionsResult) {
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
        (fanfictionRecyclerView.adapter as SyncedAdapter).fanfictionList = fanfictionList
        profileFanfictionsViewFlipper.displayedChild = DISPLAY_LIST
    }

    override fun onOptionsClicked(fanfiction: FanfictionSyncedUIModel) {
        val optionsFragment = OptionsBottomSheetDialogFragment.newInstance(
            fanfiction.id,
            fanfiction.title,
            fanfiction.publishedDate,
            fanfiction.updatedDate,
            fanfiction.fetchedDate,
            shouldShowDeleteOption = false
        )
        optionsFragment.setTargetFragment(this, 1000)
        fragmentManager?.let {
            optionsFragment.show(it, "fanfiction_options")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            data?.let { intent ->
                val fanfictionId = intent.getStringExtra(
                    OptionsBottomSheetDialogFragment.EXTRA_FANFICTION_ID
                )
                when (intent.getStringExtra(
                    OptionsBottomSheetDialogFragment.EXTRA_ACTION
                )) {
                    OptionsBottomSheetDialogFragment.EXTRA_ACTION_DETAILS -> fetchFanfictionInformation(
                        fanfictionId
                    )
                    OptionsBottomSheetDialogFragment.EXTRA_ACTION_PDF -> println("EXTRA_ACTION_PDF")
                    OptionsBottomSheetDialogFragment.EXTRA_ACTION_EPUB -> println(
                        "EXTRA_ACTION_EPUB"
                    )
                    OptionsBottomSheetDialogFragment.EXTRA_ACTION_DELETE -> println("Nope")
                }
            }
        }
    }

    private fun fetchFanfictionInformation(fanfictionId: String) {
        progressBar.visibility = View.VISIBLE
        viewModel.loadFanfictionInfo(fanfictionId)
    }
}
