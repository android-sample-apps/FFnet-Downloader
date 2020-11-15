package fr.ffnet.downloader.synced

import android.Manifest
import android.app.AlertDialog
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
import fr.ffnet.downloader.synced.SyncedViewModel.SyncedFanfictionsResult
import fr.ffnet.downloader.synced.injection.SyncedModule
import fr.ffnet.downloader.utils.FanfictionOpener
import fr.ffnet.downloader.utils.OnFanfictionActionsListener
import fr.ffnet.downloader.utils.SwipeToDeleteCallback
import kotlinx.android.synthetic.main.fragment_synced.*
import javax.inject.Inject

class SyncedFragment : Fragment(), OnFanfictionActionsListener {

    @Inject lateinit var syncedViewModel: SyncedViewModel

    @Inject lateinit var optionsViewModel: OptionsViewModel
    @Inject lateinit var fanfictionOpener: FanfictionOpener

    private val absolutePath: String by lazy {
        requireContext().getExternalFilesDir(
            Environment.DIRECTORY_DOCUMENTS
        )?.absolutePath ?: throw IllegalArgumentException()
    }

    private var exportFanfictionId: String = ""

    companion object {
        private const val DISPLAY_SYNCED_FANFICTIONS = 0
        private const val DISPLAY_NO_SYNCED_FANFICTIONS = 1

        private const val STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE
        private const val EXPORT_EPUB_REQUEST = 2000
        private const val EXPORT_PDF_REQUEST = 2001
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        requireActivity().title = resources.getString(R.string.synced_title)
        return inflater.inflate(R.layout.fragment_synced, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        MainApplication.getComponent(requireContext())
            .plus(SyncedModule(this))
            .inject(this)


        syncedFanfictionsRecyclerView.adapter = SyncedAdapter(this)
        val swiper = object : SwipeToDeleteCallback(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                (syncedFanfictionsRecyclerView.adapter as SyncedAdapter).unsync(
                    viewHolder.adapterPosition
                )
            }
        }
        val itemTouchHelper = ItemTouchHelper(swiper)
        itemTouchHelper.attachToRecyclerView(syncedFanfictionsRecyclerView)

        syncedViewModel.loadFanfictions()
        syncedViewModel.getFanfictionList().observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is SyncedFanfictionsResult.NoSyncedFanfictions -> {
                    fanfictionViewFlipper.displayedChild = DISPLAY_NO_SYNCED_FANFICTIONS
                }
                is SyncedFanfictionsResult.SyncedFanfictions -> {
                    (syncedFanfictionsRecyclerView.adapter as SyncedAdapter).fanfictionList = result.fanfictionList
                    fanfictionViewFlipper.displayedChild = DISPLAY_SYNCED_FANFICTIONS
                }
            }
        })
        optionsViewModel.getFile.observe(viewLifecycleOwner, Observer { (fileName, absolutePath) ->
            fanfictionOpener.openFile(fileName, absolutePath)
        })

        optionsViewModel.navigateToFanfiction.observe(viewLifecycleOwner, Observer { fanfictionId ->
            startActivity(FanfictionActivity.intent(requireContext(), fanfictionId))
        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            when (requestCode) {
                EXPORT_EPUB_REQUEST -> exportEpub()
                EXPORT_PDF_REQUEST -> exportPdf()
            }
        } else {
            AlertDialog
                .Builder(context)
                .setTitle(R.string.export_permission_title)
                .setMessage(R.string.export_permission_content)
                .setPositiveButton(R.string.export_permission_grant) { _, _ ->
                    checkPermission(requestCode)
                }
                .setNegativeButton(R.string.export_permission_deny) { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
                .show()
        }
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
