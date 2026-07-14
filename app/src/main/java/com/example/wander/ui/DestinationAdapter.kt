package com.example.wander.ui

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.wander.R
import com.example.wander.api.Destination

class DestinationAdapter(private var destinationList: List<Destination>) :
    RecyclerView.Adapter<DestinationAdapter.DestinationViewHolder>() {

    class DestinationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvDestinationName)
        val tvLocation: TextView = itemView.findViewById(R.id.tvDestinationLocation)
        val ivDestinationImage: ImageView = itemView.findViewById(R.id.ivDestinationImage)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DestinationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_destination, parent, false)
        return DestinationViewHolder(view)
    }
    override fun onBindViewHolder(holder: DestinationViewHolder, position: Int) {
        val currentDestination = destinationList[position]
        holder.tvName.text = currentDestination.name
        holder.tvLocation.text = "${currentDestination.location}, ${currentDestination.province}"

        if (!currentDestination.imageUrl.isNullOrEmpty()) {
            val fullImageUrl = com.example.wander.api.WanderClient.BASE_URL + currentDestination.imageUrl

            Glide.with(holder.itemView.context)
                .load(fullImageUrl)
                .centerCrop()
                .into(holder.ivDestinationImage)
        } else {
            Glide.with(holder.itemView.context).clear(holder.ivDestinationImage)
            holder.ivDestinationImage.setImageDrawable(null)
            holder.ivDestinationImage.setBackgroundColor(android.graphics.Color.parseColor("#DDDDDD"))
        }
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra("DESTINATION_ID", currentDestination.destinationId)
            intent.putExtra("DESTINATION_NAME", currentDestination.name)
            intent.putExtra("DESTINATION_LOCATION", "${currentDestination.location}, ${currentDestination.province}")
            intent.putExtra("DESTINATION_DESC", currentDestination.description)
            intent.putExtra("DESTINATION_IMAGE", currentDestination.imageUrl)
            intent.putExtra("DESTINATION_HOURS", currentDestination.openingHours)

            context.startActivity(intent)
        }
    }
    override fun getItemCount(): Int {
        return destinationList.size
    }
    fun updateList(newList: List<Destination>) {
        destinationList = newList
        notifyDataSetChanged()
    }
}