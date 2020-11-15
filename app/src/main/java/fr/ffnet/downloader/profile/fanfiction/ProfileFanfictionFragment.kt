package fr.ffnet.downloader.profile.fanfiction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import fr.ffnet.downloader.R
import fr.ffnet.downloader.common.MainApplication
import fr.ffnet.downloader.profile.ProfileViewModel.ProfileFanfictionsResult
import fr.ffnet.downloader.profile.fanfiction.injection.ProfileFanfictionModule
import fr.ffnet.downloader.synced.FanfictionSyncedUIModel
import fr.ffnet.downloader.synced.OptionsController
import fr.ffnet.downloader.synced.SyncedAdapter
import fr.ffnet.downloader.utils.SwipeToDeleteCallback
import kotlinx.android.synthetic.main.fragment_profile_fanfictions.*
import javax.inject.Inject

class ProfileFanfictionFragment : Fragment() {

    @Inject lateinit var profileViewModel: ProfileFanfictionViewModel
    @Inject lateinit var optionsController: OptionsController

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_profile_fanfictions, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        MainApplication.getComponent(requireContext())
            .plus(ProfileFanfictionModule(this))
            .inject(this)

        fanfictionRecyclerView.adapter = SyncedAdapter(optionsController)

        profileViewModel.loadFavoriteFanfictions()
        profileViewModel.loadMyFanfictions()

        setListeners()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        optionsController.onRequestPermissionsResult(requestCode, grantResults)
    }

    private fun setListeners() {
        if (isFavorites) {
            profileViewModel.getMyFavoritesList().observe(viewLifecycleOwner, Observer {
                onProfileFanfictionsResult(it)
            })
        } else {
            profileViewModel.getMyStoriesList().observe(viewLifecycleOwner, Observer {
                onProfileFanfictionsResult(it)
            })
        }
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
