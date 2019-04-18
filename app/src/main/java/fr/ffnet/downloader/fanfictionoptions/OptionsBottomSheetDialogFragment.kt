package fr.ffnet.downloader.fanfictionoptions

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import fr.ffnet.downloader.R
import kotlinx.android.synthetic.main.options_fanfiction.*

class OptionsBottomSheetDialogFragment : BottomSheetDialogFragment() {

    companion object {

        const val EXTRA_FANFICTION_ID = "EXTRA_FANFICTION_ID"
        const val EXTRA_FANFICTION_TITLE = "EXTRA_FANFICTION_TITLE"
        const val EXTRA_SHOW_DELETE = "EXTRA_SHOW_DELETE"
        const val EXTRA_DATE_PUBLISHED = "EXTRA_DATE_PUBLISHED"
        const val EXTRA_DATE_UPDATED = "EXTRA_DATE_UPDATED"
        const val EXTRA_DATE_SYNCED = "EXTRA_DATE_SYNCED"

        const val EXTRA_ACTION = "EXTRA_ACTION"
        const val EXTRA_ACTION_DETAILS = "ACTION_SEE_DETAILS"
        const val EXTRA_ACTION_PDF = "ACTION_EXPORT_PDF"
        const val EXTRA_ACTION_EPUB = "ACTION_EXPORT_EPUB"
        const val EXTRA_ACTION_DELETE = "ACTION_DELETE"

        fun newInstance(
            fanfictionId: String,
            title: String,
            publishedDate: String,
            updatedDate: String,
            fetchedDate: String,
            shouldShowDeleteOption: Boolean = true
        ): OptionsBottomSheetDialogFragment {
            return OptionsBottomSheetDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(EXTRA_FANFICTION_ID, fanfictionId)
                    putString(EXTRA_FANFICTION_TITLE, title)
                    putString(EXTRA_DATE_PUBLISHED, publishedDate)
                    putString(EXTRA_DATE_UPDATED, updatedDate)
                    putString(EXTRA_DATE_SYNCED, fetchedDate)
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

        fanfictionTitle.text = arguments?.getString(EXTRA_FANFICTION_TITLE) ?: "N/A"

        arguments?.getBoolean(EXTRA_SHOW_DELETE)?.let { shouldShowDeleteOption ->
            if (!shouldShowDeleteOption) {
                optionDeleteTextView.visibility = View.GONE
            }
        }
        arguments?.getString(EXTRA_DATE_PUBLISHED)?.let {
            publishedDateLabelTextView.visibility = View.VISIBLE
            publishedDateValueTextView.visibility = View.VISIBLE
            publishedDateValueTextView.text = it
        }
        arguments?.getString(EXTRA_DATE_UPDATED)?.let {
            updatedDateLabelTextView.visibility = View.VISIBLE
            updatedDateValueTextView.visibility = View.VISIBLE
            updatedDateValueTextView.text = it
        }
        arguments?.getString(EXTRA_DATE_SYNCED)?.let {
            syncedDateLabelTextView.visibility = View.VISIBLE
            syncedDateValueTextView.visibility = View.VISIBLE
            syncedDateValueTextView.text = it
        }

        optionSeeDetailsTextView.setOnClickListener {
            setResultAndFinish(Intent().apply {
                putExtra(
                    EXTRA_ACTION,
                    EXTRA_ACTION_DETAILS
                )
            })
        }
        optionExportPDFTextView.setOnClickListener {
            setResultAndFinish(Intent().apply {
                putExtra(
                    EXTRA_ACTION,
                    EXTRA_ACTION_PDF
                )
            })
        }
        optionExportEPUBTextView.setOnClickListener {
            setResultAndFinish(Intent().apply {
                putExtra(
                    EXTRA_ACTION,
                    EXTRA_ACTION_EPUB
                )
            })
        }
        optionDeleteTextView.setOnClickListener {
            setResultAndFinish(Intent().apply {
                putExtra(
                    EXTRA_ACTION,
                    EXTRA_ACTION_DELETE
                )
            })
        }
    }

    private fun setResultAndFinish(intent: Intent) {
        intent.putExtra(EXTRA_FANFICTION_ID, arguments?.getString(EXTRA_FANFICTION_ID))
        targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
        dismiss()
    }
}
