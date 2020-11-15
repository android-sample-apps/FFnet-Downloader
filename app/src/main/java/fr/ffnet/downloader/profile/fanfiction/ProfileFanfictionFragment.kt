package fr.ffnet.downloader.profile.fanfiction

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import fr.ffnet.downloader.R
import fr.ffnet.downloader.common.FFLogger
import fr.ffnet.downloader.common.MainApplication
import fr.ffnet.downloader.fanfiction.FanfictionActivity
import fr.ffnet.downloader.fanfictionoptions.OptionsViewModel
import fr.ffnet.downloader.profile.ProfileViewModel.ProfileFanfictionsResult
import fr.ffnet.downloader.profile.fanfiction.injection.ProfileFanfictionModule
import fr.ffnet.downloader.synced.FanfictionSyncedUIModel
import fr.ffnet.downloader.synced.SyncedAdapter
import fr.ffnet.downloader.utils.FanfictionOpener
import fr.ffnet.downloader.utils.OnFanfictionActionsListener
import fr.ffnet.downloader.utils.SwipeToDeleteCallback
import kotlinx.android.synthetic.main.fragment_profile_fanfictions.*
import kotlinx.android.synthetic.main.fragment_synced.*
import javax.inject.Inject

class ProfileFanfictionFragment : Fragment(), OnFanfictionActionsListener {

    @Inject lateinit var profileViewModel: ProfileFanfictionViewModel

    @Inject lateinit var optionsViewModel: OptionsViewModel
    @Inject lateinit var fanfictionOpener: FanfictionOpener

    private val absolutePath: String by lazy {
        requireContext().getExternalFilesDir(
            Environment.DIRECTORY_DOCUMENTS
        )?.absolutePath ?: throw IllegalArgumentException()
    }

    companion object {

        private const val DISPLAY_NO_FANFICTIONS = 0
        private const val DISPLAY_LIST = 1
        private const val EXTRA_IS_FAVORITE = "EXTRA_IS_FAVORITE"

        private const val STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE
        private const val EXPORT_EPUB_REQUEST = 2000
        private const val EXPORT_PDF_REQUEST = 2001

        fun newInstance(isFavorites: Boolean): ProfileFanfictionFragment {
            return ProfileFanfictionFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(EXTRA_IS_FAVORITE, isFavorites)
                }
            }
        }
    }

    private val isFavorites by lazy { arguments?.getBoolean(EXTRA_IS_FAVORITE, false) ?: false }
    private var exportFanfictionId: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_profile_fanfictions, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        MainApplication.getComponent(requireContext())
            .plus(
                ProfileFanfictionModule(
                    this
                )
            )
            .inject(this)

        fanfictionRecyclerView.adapter = SyncedAdapter(this)
        val swiper = object : SwipeToDeleteCallback(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                (syncedFanfictionsRecyclerView.adapter as SyncedAdapter).unsync(
                    viewHolder.adapterPosition
                )
            }
        }
        val itemTouchHelper = ItemTouchHelper(swiper)
        itemTouchHelper.attachToRecyclerView(fanfictionRecyclerView)

        profileViewModel.loadFavoriteFanfictions()
        profileViewModel.loadMyFanfictions()

        if (isFavorites) {
            profileViewModel.getMyFavoritesList().observe(viewLifecycleOwner, Observer {
                onProfileFanfictionsResult(it)
            })
        } else {
            profileViewModel.getMyStoriesList().observe(viewLifecycleOwner, Observer {
                onProfileFanfictionsResult(it)
            })
        }
        optionsViewModel.getFile.observe(viewLifecycleOwner, Observer { (fileName, absolutePath) ->
            fanfictionOpener.openFile(fileName, absolutePath)
        })
        optionsViewModel.navigateToFanfiction.observe(viewLifecycleOwner, Observer { fanfictionId ->
            startActivity(FanfictionActivity.intent(requireContext(), fanfictionId))
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

    override fun onFetchInformation(fanfiction: FanfictionSyncedUIModel) {
        FFLogger.d(FFLogger.EVENT_KEY, "Opening details for ${fanfiction.id}")
        optionsViewModel.loadFanfictionInfo(fanfiction.id)
    }

    override fun onExportPdf(fanfiction: FanfictionSyncedUIModel) {
        exportFanfictionId = fanfiction.id
        exportPdf()
    }

    override fun onExportEpub(fanfiction: FanfictionSyncedUIModel) {
        exportFanfictionId = fanfiction.id
        exportEpub()
    }

    override fun onUnsync(fanfiction: FanfictionSyncedUIModel) {
        FFLogger.d(FFLogger.EVENT_KEY, "Unsync ${fanfiction.id}")
        optionsViewModel.unsyncFanfiction(fanfiction.id)
    }

    private fun checkPermission(requestCode: Int): Boolean {
        return if (ActivityCompat.checkSelfPermission(
                requireContext(),
                STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(STORAGE), requestCode)
            false
        } else true
    }

    private fun exportEpub() {
        if (checkPermission(EXPORT_EPUB_REQUEST)) {
            FFLogger.d(FFLogger.EVENT_KEY, "Export EPUB for $exportFanfictionId")
            optionsViewModel.buildEpub(absolutePath, exportFanfictionId)
        }
    }

    private fun exportPdf() {
        if (checkPermission(EXPORT_PDF_REQUEST)) {
            FFLogger.d(FFLogger.EVENT_KEY, "Export PDF for $exportFanfictionId")
            optionsViewModel.buildPdf(absolutePath, exportFanfictionId)
        }
    }
}
