package com.example.cs481traveljournal

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.util.*

class JournalEntry : AppCompatActivity() {

    private lateinit var travelPic: ImageView
    private lateinit var imageUri: Uri
    private lateinit var storageReference: StorageReference
    private lateinit var storage: FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_journal_entry)
        travelPic = findViewById(R.id.travelPic)

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
}