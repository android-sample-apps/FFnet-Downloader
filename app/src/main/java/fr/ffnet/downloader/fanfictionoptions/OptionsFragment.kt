package fr.ffnet.downloader.fanfictionoptions

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import fr.ffnet.downloader.R
import fr.ffnet.downloader.common.MainApplication
import fr.ffnet.downloader.fanfiction.FanfictionActivity
import fr.ffnet.downloader.utils.FanfictionOpener
import kotlinx.android.synthetic.main.options_fanfiction.*
import javax.inject.Inject

class OptionsFragment : BottomSheetDialogFragment() {

    @Inject lateinit var viewModel: OptionsViewModel
    @Inject lateinit var fanfictionOpener: FanfictionOpener

    private val absolutePath: String by lazy {
        requireContext().getExternalFilesDir(
            Environment.DIRECTORY_DOCUMENTS
        )?.absolutePath ?: throw IllegalArgumentException()
    }
    private val fanfictionId by lazy {
        val fanfictionId = arguments?.getString(EXTRA_FANFICTION_ID)
        if (fanfictionId.isNullOrBlank()) {
            dismiss()
            ""
        } else {
            fanfictionId
        }
    }
    private val shouldShowDeleteOption by lazy { arguments?.getBoolean(EXTRA_SHOW_DELETE) ?: false }

    companion object {
        const val EXTRA_FANFICTION_ID = "EXTRA_FANFICTION_ID"
        const val EXTRA_SHOW_DELETE = "EXTRA_SHOW_DELETE"
        private const val STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE
        private const val EXPORT_EPUB_REQUEST = 2000
        private const val EXPORT_PDF_REQUEST = 2001
        fun newInstance(
            fanfictionId: String,
            shouldShowDeleteOption: Boolean = true
        ): OptionsFragment {
            return OptionsFragment().apply {
                arguments = Bundle().apply {
                    putString(EXTRA_FANFICTION_ID, fanfictionId)
                    putBoolean(EXTRA_SHOW_DELETE, shouldShowDeleteOption)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.options_fanfiction, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        MainApplication.getComponent(requireContext())
            .plus(OptionsModule(this))
            .inject(this)

        fanfictionOpener = FanfictionOpener(
            requireContext()
        )
        viewModel.load(fanfictionId)
        initializeObservers()
    }

    private fun initializeObservers() {
        viewModel.navigateToFanfiction.observe(viewLifecycleOwner, Observer {
            startActivity(FanfictionActivity.intent(requireContext(), fanfictionId))
            dismiss()
        })

        viewModel.getDisplayModel().observe(viewLifecycleOwner, Observer { displayModel ->

            fanfictionTitle.text = displayModel.title

            val watchingDrawable = if (displayModel.isWatching) R.drawable.ic_eye_watching else R.drawable.ic_eye_not_watching
            watchingImageView.background = ContextCompat.getDrawable(
                requireContext(),
                watchingDrawable
            )

            optionDeleteTextView.isVisible = shouldShowDeleteOption

            publishedDateValueTextView.text = displayModel.publishedDate

            updatedDateValueTextView.text = displayModel.updatedDate

            fetchedDateLabelTextView.isVisible = displayModel.shouldShowFetchedDate
            fetchedDateValueTextView.isVisible = displayModel.shouldShowFetchedDate
            fetchedDateValueTextView.text = displayModel.fetchedDate

            optionSeeDetailsTextView.setOnClickListener {
                viewModel.loadFanfictionInfo(fanfictionId)
            }
            optionExportPDFTextView.setOnClickListener {
                exportPdf()
            }
            optionExportEPUBTextView.setOnClickListener {
                exportEpub()
            }
            optionDeleteTextView.setOnClickListener {
                viewModel.unsyncFanfiction(fanfictionId)
                dismiss()
            }
            watchingImageView.setOnClickListener {
                viewModel.onWatchingChanged(fanfictionId)
            }
        })
        viewModel.getFile.observe(viewLifecycleOwner, Observer { (fileName, absolutePath) ->
            fanfictionOpener.openFile(fileName, absolutePath)
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
            viewModel.buildEpub(absolutePath, fanfictionId)
        }
    }

    private fun exportPdf() {
        if (checkPermission(EXPORT_PDF_REQUEST)) {
            viewModel.buildPdf(absolutePath, fanfictionId)
        }
    }
}
