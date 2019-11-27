package fr.ffnet.downloader.profile.fanfiction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import dagger.android.support.DaggerFragment
import fr.ffnet.downloader.R
import fr.ffnet.downloader.fanfictionoptions.OptionsFragment
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
    }

    override fun onOptionsClicked(fanfiction: FanfictionSyncedUIModel) {
        val optionsFragment = OptionsFragment.newInstance(
            fanfictionId = fanfiction.id,
            shouldShowDeleteOption = false
        )
        optionsFragment.setTargetFragment(this, 1000)
        optionsFragment.show(parentFragmentManager, "fanfiction_options")
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
}
