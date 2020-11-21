package fr.ffnet.downloader.profile.fanfiction

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import fr.ffnet.downloader.R
import fr.ffnet.downloader.common.MainApplication
import fr.ffnet.downloader.profile.fanfiction.ProfileFanfictionViewModel.ProfileFanfictionsResult
import fr.ffnet.downloader.profile.fanfiction.ProfileFanfictionViewModel.ProfileFanfictionsResult.ProfileHasFanfictions
import fr.ffnet.downloader.profile.fanfiction.ProfileFanfictionViewModel.ProfileFanfictionsResult.ProfileHasNoFanfictions
import fr.ffnet.downloader.profile.fanfiction.injection.ProfileFanfictionModule
import fr.ffnet.downloader.synced.FanfictionListAdapter
import fr.ffnet.downloader.synced.FanfictionUIItem
import fr.ffnet.downloader.synced.OptionsController
import fr.ffnet.downloader.synced.PermissionListener
import kotlinx.android.synthetic.main.fragment_profile_fanfictions.*
import javax.inject.Inject

class ProfileFanfictionFragment : Fragment(), PermissionListener {

    @Inject lateinit var profileViewModel: ProfileFanfictionViewModel
    @Inject lateinit var optionsController: OptionsController

    companion object {
        private const val DISPLAY_NO_FANFICTIONS = 0
        private const val DISPLAY_LIST = 1
        private const val EXTRA_IS_FAVORITE = "EXTRA_IS_FAVORITE"
        private const val EXTRA_AUTHOR_ID = "EXTRA_AUTHOR_ID"

        fun newInstance(isFavorites: Boolean, authorId: String): ProfileFanfictionFragment {
            return ProfileFanfictionFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(EXTRA_IS_FAVORITE, isFavorites)
                    putString(EXTRA_AUTHOR_ID, authorId)
                }
            }
        }
    }

    private val isFavorites by lazy { arguments?.getBoolean(EXTRA_IS_FAVORITE, false) ?: false }
    private val authorId by lazy { arguments?.getString(EXTRA_AUTHOR_ID) ?: "" }

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

        fanfictionRecyclerView.adapter = FanfictionListAdapter(optionsController)

        profileViewModel.loadFavoriteFanfictions(authorId)
        profileViewModel.loadMyFanfictions(authorId)

        setListeners()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        optionsController.onRequestPermissionsResult(requestCode, grantResults)
    }

    override fun onPermissionRequested(arrayOf: Array<String>, requestCode: Int): Boolean {
        return if (ActivityCompat.checkSelfPermission(
                requireContext(),
                OptionsController.STORAGE
            ) != PackageManager.PERMISSION_GRANTED
            && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
        ) {
            requestPermissions(arrayOf(OptionsController.STORAGE), requestCode)
            false
        } else true
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
            is ProfileHasFanfictions -> showFanfictions(result.fanfictionUIList)
            is ProfileHasNoFanfictions -> {
                profileFanfictionsViewFlipper.displayedChild = DISPLAY_NO_FANFICTIONS
            }
        }
    }

    private fun showFanfictions(fanfictionUIList: List<FanfictionUIItem.FanfictionUI>) {
        (fanfictionRecyclerView.adapter as FanfictionListAdapter).fanfictionItemList = fanfictionUIList
        profileFanfictionsViewFlipper.displayedChild = DISPLAY_LIST
    }
}
