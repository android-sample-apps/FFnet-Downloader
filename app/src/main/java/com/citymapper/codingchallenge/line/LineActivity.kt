package com.citymapper.codingchallenge.line

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.citymapper.codingchallenge.R

class LineActivity : AppCompatActivity() {

    companion object {
        private const val EXTRA_LINE = "LINE"
        fun newIntent(context: Context, line: String): Intent {
            return Intent(context, LineActivity::class.java).apply {
                putExtra(EXTRA_LINE, line)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_line)

        intent.extras?.getString(EXTRA_LINE)?.let { line ->
            
        } ?: abortActivity()
    }

    private fun abortActivity() {
        Toast.makeText(this, "No line provided", Toast.LENGTH_LONG).show()
        finish()
    }
}
