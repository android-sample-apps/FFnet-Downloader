package fr.ffnet.downloader.synced

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import fr.ffnet.downloader.R
import fr.ffnet.downloader.common.MainApplication
import fr.ffnet.downloader.fanfictionoptions.OptionsFragment
import fr.ffnet.downloader.synced.SyncedViewModel.SyncedFanfictionsResult
import fr.ffnet.downloader.synced.injection.SyncedModule
import fr.ffnet.downloader.utils.OnFanfictionOptionsListener
import kotlinx.android.synthetic.main.fragment_synced.*
import javax.inject.Inject

class SyncedFragment : Fragment(), OnFanfictionOptionsListener {

    @Inject lateinit var viewModel: SyncedViewModel

    companion object {
        private const val DISPLAY_SYNCED_FANFICTIONS = 0
        private const val DISPLAY_NO_SYNCED_FANFICTIONS = 1
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
        viewModel.loadFanfictions()
        viewModel.getFanfictionList().observe(viewLifecycleOwner, Observer { fanfictionResult ->
            when (fanfictionResult) {
                is SyncedFanfictionsResult.NoSyncedFanfictions -> showNoSyncedFanfictions()
                is SyncedFanfictionsResult.SyncedFanfictions -> showSyncedFanfictions(
                    fanfictionResult.fanfictionList
                )
            }
        })
    }

    override fun onOptionsClicked(fanfiction: FanfictionSyncedUIModel) {
        val optionsFragment = OptionsFragment.newInstance(
            fanfictionId = fanfiction.id,
            shouldShowDeleteOption = true
        )
        optionsFragment.setTargetFragment(this, 1000)
        optionsFragment.show(parentFragmentManager, "fanfiction_options")
    }

    private fun showSyncedFanfictions(fanfictionList: List<FanfictionSyncedUIModel>) {
        (syncedFanfictionsRecyclerView.adapter as SyncedAdapter).fanfictionList = fanfictionList
        fanfictionResultViewFlipper.displayedChild = DISPLAY_SYNCED_FANFICTIONS
    }

    private fun showNoSyncedFanfictions() {
        fanfictionResultViewFlipper.displayedChild = DISPLAY_NO_SYNCED_FANFICTIONS
    }
}
