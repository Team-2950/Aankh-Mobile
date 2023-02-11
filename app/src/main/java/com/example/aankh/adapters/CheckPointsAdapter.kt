package com.example.aankh.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.aankh.R
import com.example.aankh.dataModels.CheckPointsDataModel

class CheckPointsAdapter : RecyclerView.Adapter<CheckPointsAdapter.ViewHolder>() {

    private var checkPoints = ArrayList<CheckPointsDataModel>()

    fun updateCheckPoints(checkPoints: ArrayList<CheckPointsDataModel>) {

        this.checkPoints = checkPoints
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView = itemView.findViewById<TextView>(R.id.checkPointDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.checkpoints_list_item_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (checkPoints.size != 0) {
            holder.textView.text = checkPoints[position].description
        } else {
            holder.textView.text = "No check points!!"
        }
    }

    override fun getItemCount(): Int {
        return checkPoints.size
    }

}
