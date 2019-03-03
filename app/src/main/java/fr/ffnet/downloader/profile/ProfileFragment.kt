package fr.ffnet.downloader.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import dagger.android.support.DaggerFragment
import fr.ffnet.downloader.R
import kotlinx.android.synthetic.main.fragment_profile.*
import javax.inject.Inject

class ProfileFragment : DaggerFragment() {

    @Inject lateinit var viewModel: ProfileViewModel

    companion object {
        private const val DISPLAY_LIST = 0
        private const val DISPLAY_ASSOCIATE = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_profile, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getIsProfileAssociated().observe(this, Observer { isAssociated ->
            if (isAssociated) {
                profileAssociationStatusViewFlipper.displayedChild = DISPLAY_LIST
            } else {
                profileAssociationStatusViewFlipper.displayedChild = DISPLAY_ASSOCIATE
            }
        })

        fetchInformationButton.setOnClickListener {
            viewModel.associateProfile(profileUrlEditText.text.toString())
        }
    }
}
