package fr.ffnet.downloader.fanfictionoptions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.android.support.AndroidSupportInjection
import fr.ffnet.downloader.R
import kotlinx.android.synthetic.main.options_fanfiction.*
import javax.inject.Inject

class OptionsFragment : BottomSheetDialogFragment() {

    @Inject lateinit var viewModel: OptionsViewModel

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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        AndroidSupportInjection.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.options_fanfiction, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.load(fanfictionId)
        initializeObservers()
    }

    private fun initializeObservers() {
        viewModel.getDisplayModel().observe(this, Observer { displayModel ->

            fanfictionTitle.text = displayModel.title

            optionDeleteTextView.isVisible = shouldShowDeleteOption

            publishedDateValueTextView.text = displayModel.publishedDate

            updatedDateValueTextView.text = displayModel.updatedDate

            fetchedDateLabelTextView.isVisible = displayModel.shouldShowFetchedDate
            fetchedDateValueTextView.isVisible = displayModel.shouldShowFetchedDate
            fetchedDateValueTextView.text = displayModel.fetchedDate

            optionSeeDetailsTextView.setOnClickListener {
                // Open activity
            }
            optionExportPDFTextView.setOnClickListener {
                // Download PDF
            }
            optionExportEPUBTextView.setOnClickListener {
                // Download Epub
            }
            optionDeleteTextView.setOnClickListener {
                // Delete
            }
        })
    }
}
