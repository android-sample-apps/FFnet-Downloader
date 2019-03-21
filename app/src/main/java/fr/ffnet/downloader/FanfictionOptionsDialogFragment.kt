package fr.ffnet.downloader

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.options_fanfiction.*

class FanfictionOptionsDialogFragment : BottomSheetDialogFragment() {

    companion object {
        fun newInstance(
            fanfictionId: String,
            title: String,
            shouldShowDeleteOption: Boolean = true
        ): FanfictionOptionsDialogFragment {
            return FanfictionOptionsDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(EXTRA_FANFICTION_ID, fanfictionId)
                    putString(EXTRA_FANFICTION_TITLE, title)
                    putBoolean(EXTRA_SHOW_DELETE, shouldShowDeleteOption)
                }
            }
        }

        const val EXTRA_FANFICTION_ID = "EXTRA_FANFICTION_ID"
        const val EXTRA_FANFICTION_TITLE = "EXTRA_FANFICTION_TITLE"
        const val EXTRA_SHOW_DELETE = "EXTRA_SHOW_DELETE"
        const val EXTRA_ACTION = "EXTRA_ACTION"
        const val EXTRA_ACTION_DETAILS = "ACTION_SEE_DETAILS"
        const val EXTRA_ACTION_PDF = "ACTION_EXPORT_PDF"
        const val EXTRA_ACTION_EPUB = "ACTION_EXPORT_EPUB"
        const val EXTRA_ACTION_DELETE = "ACTION_DELETE"
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

        optionSeeDetailsTextView.setOnClickListener {
            setResultAndFinish(Intent().apply {
                putExtra(EXTRA_ACTION, EXTRA_ACTION_DETAILS)
            })
        }
        optionExportPDFTextView.setOnClickListener {
            setResultAndFinish(Intent().apply {
                putExtra(EXTRA_ACTION, EXTRA_ACTION_PDF)
            })
        }
        optionExportEPUBTextView.setOnClickListener {
            setResultAndFinish(Intent().apply {
                putExtra(EXTRA_ACTION, EXTRA_ACTION_EPUB)
            })
        }
        optionDeleteTextView.setOnClickListener {
            setResultAndFinish(Intent().apply {
                putExtra(EXTRA_ACTION, EXTRA_ACTION_DELETE)
            })
        }
    }

    private fun setResultAndFinish(intent: Intent) {
        intent.apply {
            putExtra(EXTRA_FANFICTION_ID, arguments?.getString(EXTRA_FANFICTION_ID))
        }
        targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
        dismiss()
    }
}
