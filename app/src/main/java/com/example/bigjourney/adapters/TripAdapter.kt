package com.example.bigjourney.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bigjourney.R
import com.example.bigjourney.model.Trip

class TripAdapter(
    private val trips: List<Trip>,
    private val onDeleteSelected: (List<Trip>) -> Unit,

) : RecyclerView.Adapter<TripAdapter.TripViewHolder>() {

    private val selectedTrips = mutableSetOf<Int>()

    class TripViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tripLocation: TextView = itemView.findViewById(R.id.tripLocationText)
        val tripDates: TextView = itemView.findViewById(R.id.tripDatesText)
        val tripItemLayout: View = itemView.findViewById(R.id.tripItemLayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.trip_item, parent, false)
        return TripViewHolder(view)
    }

    override fun onBindViewHolder(holder: TripViewHolder, position: Int) {
        val trip = trips[position]
        holder.tripLocation.text = trip.location
        holder.tripDates.text = "${trip.startDate} - ${trip.endDate}"

        // Ελέγχουμε αν το στοιχείο είναι επιλεγμένο
        holder.tripItemLayout.setBackgroundColor(
            if (selectedTrips.contains(position)) Color.LTGRAY else Color.TRANSPARENT
        )

        holder.itemView.setOnLongClickListener {
            // Προσθήκη στην επιλογή
            selectedTrips.add(position)
            onDeleteSelected(trips.filterIndexed { index, _ -> selectedTrips.contains(index) })
            notifyItemChanged(position)
            true
        }

        holder.itemView.setOnClickListener {
            // Απλά επιλέγουμε ή αφαιρούμε την επιλογή
            if (selectedTrips.contains(position)) {
                selectedTrips.remove(position)
            } else {
                selectedTrips.add(position)
            }
            onDeleteSelected(trips.filterIndexed { index, _ -> selectedTrips.contains(index) })
            notifyItemChanged(position)
        }
    }



    override fun getItemCount() = trips.size
    fun clearSelections() {
        selectedTrips.clear()
        notifyDataSetChanged()
    }
}




