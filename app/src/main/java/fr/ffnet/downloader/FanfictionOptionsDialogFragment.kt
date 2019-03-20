package fr.ffnet.downloader

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class FanfictionOptionsDialogFragment : BottomSheetDialogFragment() {

    companion object {
        fun newInstance(): FanfictionOptionsDialogFragment = FanfictionOptionsDialogFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.options_fanfiction, container, false)
    }
}
