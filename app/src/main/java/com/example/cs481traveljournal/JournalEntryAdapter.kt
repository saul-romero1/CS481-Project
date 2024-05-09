package com.example.cs481traveljournal

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.widget.ImageView
import com.bumptech.glide.Glide

class JournalEntryAdapter(var journalEntries: List<JournalEntryClass>) :
    RecyclerView.Adapter<JournalEntryAdapter.JournalEntryViewHolder>() {

    class JournalEntryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val entry: TextView = itemView.findViewById(R.id.Entry)
        val location: TextView = itemView.findViewById(R.id.location)
        val date: TextView = itemView.findViewById(R.id.date)
        val time: TextView = itemView.findViewById(R.id.time)
        val image: ImageView = itemView.findViewById(R.id.image) // Add this line
    }

    override fun onBindViewHolder(holder: JournalEntryViewHolder, position: Int) {
        val journalEntry = journalEntries[position]
        holder.entry.text = journalEntry.entry
        holder.location.text = journalEntry.location
        holder.date.text = journalEntry.date
        holder.time.text = journalEntry.time
        if (journalEntry.image != null) {
            Glide.with(holder.image.context)
                .load(journalEntry.image)
                .into(holder.image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JournalEntryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.journal_entry_item, parent, false)
        return JournalEntryViewHolder(view)
    }
    override fun getItemCount() = journalEntries.size

    fun updateData(newJournalEntries: List<JournalEntryClass>) {
    Log.d("JournalEntryAdapter", "New data size: ${newJournalEntries.size}")
    journalEntries = newJournalEntries
    Log.d("JournalEntryAdapter", "Updated data size: ${journalEntries.size}")
    notifyDataSetChanged()
    }

}