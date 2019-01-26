package com.citymapper.codingchallenge.line

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.citymapper.codingchallenge.R

class StationsAdapter(
    private var stations: List<StationModel>
) : RecyclerView.Adapter<StationViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StationViewHolder {
        return StationViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_station, parent, false
            )
        )
    }

    override fun getItemCount(): Int = stations.size

    override fun onBindViewHolder(holder: StationViewHolder, position: Int) {
        holder.bind(stations[position])
    }

    fun updateData(stations: List<StationModel>) {
        this.stations = stations
        notifyDataSetChanged()
    }
}

class StationViewHolder(
    itemView: View
) : RecyclerView.ViewHolder(itemView) {
    fun bind(station: StationModel) {
        with(itemView.findViewById<TextView>(R.id.stationNameTextView)) {
            text = station.name
            if (station.isSelectedStation) {
                setTypeface(Typeface.MONOSPACE, Typeface.BOLD)
            }
        }
    }
}
