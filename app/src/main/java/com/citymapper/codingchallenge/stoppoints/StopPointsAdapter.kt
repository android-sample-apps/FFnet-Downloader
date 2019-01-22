package com.citymapper.codingchallenge.stoppoints

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.citymapper.codingchallenge.R

class StopPointsAdapter(
    private var stopPoints: List<StopPoint>,
    private val listener: StopPointListener
) : RecyclerView.Adapter<StopPointViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): StopPointViewHolder {
        return StopPointViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_stoppoint, parent, false
            ),
            listener
        )
    }

    override fun getItemCount(): Int = stopPoints.size

    override fun onBindViewHolder(holder: StopPointViewHolder, position: Int) {
        holder.bind(stopPoints[position])
    }

    fun updateData(stopPoints: List<StopPoint>) {
        this.stopPoints = stopPoints
        notifyDataSetChanged()
    }
}

class StopPointViewHolder(
    itemView: View,
    private val listener: StopPointListener
) : RecyclerView.ViewHolder(itemView) {
    fun bind(stopPoint: StopPoint) {
        itemView.setOnClickListener {
            listener.onStopPointClicked(stopPoint)
        }
        itemView.findViewById<TextView>(R.id.stopPointTitleTextView).text = stopPoint.name
    }
}

interface StopPointListener {
    fun onStopPointClicked(stopPoint: StopPoint)
}
