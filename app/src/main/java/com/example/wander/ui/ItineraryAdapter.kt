package com.example.wander.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.wander.R
import com.example.wander.api.UserItineraryItem
import java.text.SimpleDateFormat
import java.util.Locale

class ItineraryAdapter(
    private val items: MutableList<UserItineraryItem>,
    private val onDeleteClick: (Int, Int) -> Unit
) : RecyclerView.Adapter<ItineraryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDayHeader: TextView = view.findViewById(R.id.tvDayHeader)
        val tvDateHeader: TextView = view.findViewById(R.id.tvDateHeader)
        val tvTime: TextView = view.findViewById(R.id.tvTime)
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvLocation: TextView = view.findViewById(R.id.tvLocation)
        val ivDestination: ImageView = view.findViewById(R.id.ivDestination)
        val ivDelete: ImageView = view.findViewById(R.id.ivDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_itinerary, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = items[position]

        val showHeader = position == 0 || items[position - 1].scheduledDate != currentItem.scheduledDate

        if (showHeader) {
            holder.tvDayHeader.visibility = View.VISIBLE
            holder.tvDateHeader.visibility = View.VISIBLE
            holder.tvDayHeader.text = "Trip Date"
            holder.tvDateHeader.text = formatDate(currentItem.scheduledDate)

            val layoutParams = holder.tvDayHeader.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.topMargin = if (position == 0) 0 else 60
            holder.tvDayHeader.layoutParams = layoutParams
        } else {
            holder.tvDayHeader.visibility = View.GONE
            holder.tvDateHeader.visibility = View.GONE
        }

        holder.tvTime.text = formatTime(currentItem.scheduledTime)
        holder.tvName.text = currentItem.name
        holder.tvLocation.text = currentItem.location

        if (!currentItem.imageUrl.isNullOrEmpty()) {
            val fullImageUrl = com.example.wander.api.WanderClient.BASE_URL + currentItem.imageUrl

            Glide.with(holder.itemView.context)
                .load(fullImageUrl)
                .centerCrop()
                .into(holder.ivDestination)
        } else {
            Glide.with(holder.itemView.context).clear(holder.ivDestination)
            holder.ivDestination.setImageDrawable(null)
            holder.ivDestination.setBackgroundColor(android.graphics.Color.parseColor("#DDDDDD"))
        }

        holder.ivDelete.setOnClickListener {
            onDeleteClick(currentItem.itineraryId, position)
        }
    }

    override fun getItemCount(): Int = items.size

    fun removeItem(position: Int) {
        items.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, items.size)
    }

    private fun formatDate(rawDate: String): String {
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val formatter = SimpleDateFormat("MMM d", Locale.getDefault())
            val date = parser.parse(rawDate)
            if (date != null) formatter.format(date) else rawDate
        } catch (e: Exception) {
            rawDate
        }
    }

    private fun formatTime(rawTime: String): String {
        return try {
            val parser = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            val formatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
            val time = parser.parse(rawTime)
            if (time != null) formatter.format(time) else rawTime
        } catch (e: Exception) {
            rawTime
        }
    }
}