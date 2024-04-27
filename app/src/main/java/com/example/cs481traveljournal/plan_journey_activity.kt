package com.example.cs481traveljournal

import MapFragment
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.datepicker.MaterialDatePicker
import java.util.Date
import java.util.Locale

class plan_journey_activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        enableEdgeToEdge()
        setContentView(R.layout.activity_plan_journey)

        supportFragmentManager.beginTransaction()
            .replace(R.id.cMap, MapFragment())
            .commit()

        findViewById<TextView>(R.id.tv_Date).setOnClickListener {
            showDateRangePicker()
        }



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    private fun showDateRangePicker() {
        val dateRangePicker = MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText("select Date").build()

        dateRangePicker.show(supportFragmentManager,"date_range_picker")

        dateRangePicker.addOnPositiveButtonClickListener {
            datePicked-> val startDate = datePicked.first
            val endDate = datePicked.second
            findViewById<TextView>(R.id.tv_Date).text = "From: ${convertLongtoDate(startDate)}" +
                    "\nTo: ${convertLongtoDate(endDate)}"
        }
        dateRangePicker.addOnNegativeButtonClickListener {
            dateRangePicker.dismiss()
        }



    }
    private fun convertLongtoDate(time: Long):String{
        val date = Date(time)
        val format = android.icu.text.SimpleDateFormat("MMM dd,yyyy", Locale.getDefault())
        return format.format(date)

    }

}