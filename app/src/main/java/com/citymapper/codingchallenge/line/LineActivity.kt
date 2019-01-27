package com.citymapper.codingchallenge.line

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.citymapper.codingchallenge.MainApplication
import com.citymapper.codingchallenge.R
import com.nicolasmouchel.executordecorator.MutableDecorator
import kotlinx.android.synthetic.main.activity_line.*
import javax.inject.Inject

class LineActivity : AppCompatActivity(), LineView {

    @Inject lateinit var controller: LineController
    @Inject lateinit var decorator: MutableDecorator<LineView>

    private lateinit var lineId: String
    private lateinit var stationId: String

    private lateinit var adapter: StationsAdapter

    companion object {
        private const val EXTRA_LINE = "LINE"
        private const val EXTRA_STATION = "STATION"
        fun newIntent(context: Context, lineId: String, stationId: String): Intent {
            return Intent(context, LineActivity::class.java).apply {
                putExtra(EXTRA_LINE, lineId)
                putExtra(EXTRA_STATION, stationId)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_line)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        MainApplication.getComponent(this).plus(LineModule()).inject(this)
        decorator.mutate(this)
        intent.extras?.getString(EXTRA_LINE)?.let { line ->
            lineId = line
        } ?: abortActivity()
        intent.extras?.getString(EXTRA_STATION)?.let { station ->
            stationId = station
        } ?: abortActivity()

        adapter = StationsAdapter(emptyList())
        stationsRecyclerView.layoutManager = LinearLayoutManager(this)
        stationsRecyclerView.adapter = adapter
        controller.loadLine(lineId, stationId)
    }

    override fun onDestroy() {
        decorator.mutate(null)
        super.onDestroy()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun showLine(line: LineModel) {
        Log.d("LINE", "Loaded line ${line.name} with ${line.stations.size} stations")
        adapter.updateData(line.stations)
    }

    private fun abortActivity() {
        Toast.makeText(this, "No line provided", Toast.LENGTH_LONG).show()
        finish()
    }
}
