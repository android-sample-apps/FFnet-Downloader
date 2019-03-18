package fr.ffnet.downloader

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.snackbar.Snackbar
import dagger.android.support.DaggerAppCompatActivity
import fr.ffnet.downloader.common.ViewModelFactory
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    @Inject lateinit var viewModelFactory: ViewModelFactory<MainViewModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(
            MainViewModel::class.java
        )
        viewModel.loadErrors()

        setContentView(R.layout.activity_main)
        navigationView.setupWithNavController(findNavController(R.id.mainNavFragment))

        viewModel.getErrors().observe(this, Observer { error ->
            error?.let {
                Snackbar.make(
                    containerFrameLayout, error.message, Snackbar.LENGTH_LONG
                ).addCallback(object : Snackbar.Callback() {
                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        viewModel.consumeError(error.id)
                    }
                })
            }
        })
    }
}
