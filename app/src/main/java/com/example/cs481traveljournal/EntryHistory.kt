package com.example.cs481traveljournal

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class EntryHistory : AppCompatActivity() {

    private lateinit var journalEntriesRecyclerView: RecyclerView
    private lateinit var journalEntriesAdapter: JournalEntryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_entry_history)

        journalEntriesRecyclerView = findViewById(R.id.recyclerView)
        journalEntriesAdapter = JournalEntryAdapter(ArrayList())
        journalEntriesRecyclerView.adapter = journalEntriesAdapter
        journalEntriesRecyclerView.layoutManager = LinearLayoutManager(this)

        showData()

        // Set up the back button
        findViewById<Button>(R.id.backButton).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }


fun showData() {
    val db = FirebaseFirestore.getInstance()
    db.collection("Journal Entries")
        .addSnapshotListener { value, error ->
            if (error != null) {
                Log.w("EntryHistory", "Listen failed.", error)
                return@addSnapshotListener
            }

            if (value != null) {
                Log.d("EntryHistory", "Documents fetched: ${value.documents.size}")
                val journalEntries = ArrayList<JournalEntryClass>()
                for (document in value.documents) {
                    val location = document.getString("Location")
                    val date = document.getString("Date")
                    val time = document.getString("Time")
                    val entry = document.getString("Entry")
                    val image = document.getString("Image")
                    journalEntries.add(JournalEntryClass(location, date, time, entry,image))
                    Log.d("EntryHistory", "${document.id} => ${document.data}")
                }
                journalEntriesAdapter.updateData(journalEntries)
            }
        }
    }
}
