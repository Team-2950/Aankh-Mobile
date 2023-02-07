package com.example.aankh.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.aankh.R

class CheckPointsAdapter : RecyclerView.Adapter<CheckPointsAdapter.ViewHolder>() {

    private val arrayList = listOf<String>(
        "temp1",
        "temp2",
        "temp1",
        "temp2",
        "temp1",
        "temp2",
        "temp1",
        "temp2",
        "temp1",
        "temp2"
    )

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
        holder.textView.text = arrayList[position]
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

}
