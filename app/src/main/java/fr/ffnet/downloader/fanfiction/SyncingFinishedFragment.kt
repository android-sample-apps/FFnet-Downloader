package fr.ffnet.downloader.fanfiction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import fr.ffnet.downloader.R
import kotlinx.android.synthetic.main.fragment_syncing_finished.*

interface SyncingFinishedListener {
    fun onExportPdf()
    fun onExportEpub()
}

class SyncingFinishedFragment : BottomSheetDialogFragment() {

    companion object {
        fun newIntent(): SyncingFinishedFragment {
            return SyncingFinishedFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_syncing_finished, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        exportPdfImageView.setOnClickListener {
            (requireActivity() as SyncingFinishedListener).onExportPdf()
            dismiss()
        }
        exportEpubImageView.setOnClickListener {
            (requireActivity() as SyncingFinishedListener).onExportEpub()
            dismiss()
        }
    }
}
