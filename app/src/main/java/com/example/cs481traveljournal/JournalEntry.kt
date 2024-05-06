package com.example.cs481traveljournal

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.libraries.places.api.model.LocalDate
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class JournalEntry : AppCompatActivity() {

    private lateinit var travelPic: ImageView
    private lateinit var imageUri: Uri
    private lateinit var storageReference: StorageReference
    private lateinit var storage: FirebaseStorage
    val dateFormatter = DateTimeFormatter.ofPattern("MMM/dd/yyyy")
    val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")
    var currentDate = LocalDateTime.now()
    private val handler = Looper.myLooper()?.let { Handler(it) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_journal_entry)
        findViewById<Button>(R.id.bBack).setOnClickListener {
            startActivity(Intent(this,MainActivity::class.java))
        }
        travelPic = findViewById(R.id.travelPic)

        updateDateTime()
        handler?.postDelayed(updateRunnable, 500)

        findViewById<TextView>(R.id.tv_Date).text = "${currentDate.format(dateFormatter)}\n" +
                "${currentDate.format(timeFormatter)}"

        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference

        travelPic.setOnClickListener {
            choosePicture()
        }
    }

    private fun choosePicture() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.data != null) {
            imageUri = data.data!!
            travelPic.setImageURI(imageUri)
            uploadPicture()
        }
    }

    private fun uploadPicture() {
        val pd = ProgressDialog(this@JournalEntry)
        pd.setTitle("Uploading Image...")
        pd.show()

        val randomKey = UUID.randomUUID().toString()
        val riversRef = storageReference.child("images/$randomKey")

        val locationText = findViewById<EditText>(R.id.locationText).text.toString()
        val metadata = StorageMetadata.Builder()
            .setContentType("image/jpeg")
            .setCustomMetadata("text", locationText)
            .build()

        riversRef.putFile(imageUri, metadata)
            .addOnSuccessListener { taskSnapshot ->
                pd.dismiss()
                Snackbar.make(
                    findViewById(android.R.id.content),
                    "Image uploaded",
                    Snackbar.LENGTH_LONG
                ).show()
            }
            .addOnFailureListener { exception ->
                pd.dismiss()
                Toast.makeText(applicationContext, "Failed to upload", Toast.LENGTH_LONG).show()
            }
            .addOnProgressListener { taskSnapshot ->
                val progressPercent =
                    100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount
                pd.setMessage("Progress: ${progressPercent.toInt()}%")
            }
    }
    private val updateRunnable = object : Runnable {
        override fun run() {
            updateDateTime()
            handler?.postDelayed(this, 500)
        }
    }

    private fun updateDateTime() {
        var currentDateTime = LocalDateTime.now()

        findViewById<TextView>(R.id.tv_Date).text = "${currentDateTime.format(dateFormatter)}\n" +
                "${currentDateTime.format(timeFormatter)}"
    }

    override fun onDestroy() {
        super.onDestroy()
        // Remove pending updates to avoid memory leaks
        handler?.removeCallbacks(updateRunnable)
    }

}