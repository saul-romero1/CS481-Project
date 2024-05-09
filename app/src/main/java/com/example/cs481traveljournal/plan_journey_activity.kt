package com.example.cs481traveljournal

import MapFragment
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.material.datepicker.MaterialDatePicker
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.text.NumberFormat
import java.util.Date
import java.util.Locale

class plan_journey_activity : AppCompatActivity() {
    private val client = OkHttpClient()
    private lateinit var mapFragment: MapFragment


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        enableEdgeToEdge()
        setContentView(R.layout.activity_plan_journey)
        supportFragmentManager.beginTransaction()
            .replace(R.id.cMap, MapFragment())
            .commit()

        Places.initialize(applicationContext,getString(R.string.google_api_key)) //2
       val autocompleteFragment = supportFragmentManager.findFragmentById(R.id.autocomplete_fragment)
                as AutocompleteSupportFragment
        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.ADDRESS, Place.Field.LAT_LNG))
        autocompleteFragment.setOnPlaceSelectedListener(object :PlaceSelectionListener{
            override fun onError(p0: Status) {

                Toast.makeText(applicationContext, "Error while searching", Toast.LENGTH_SHORT).show()
            }


            override fun onPlaceSelected(place: Place) {
                val viewModel = ViewModelProvider(this@plan_journey_activity).get(planJourneyViewModel::class.java)
                val mGeocoder = Geocoder(applicationContext, Locale.getDefault())
                place.latLng?.let { latLng ->
                        supportFragmentManager.findFragmentById(R.id.cMap)?.let { fragment ->
                            (fragment as? MapFragment)?.zoomOnMap(latLng)
                        }
                    try {
                        val addrList: MutableList<Address>? =
                            mGeocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                        if (addrList != null && addrList.isNotEmpty()) {
                            val countryCode = addrList[0].countryCode
                            if (addrList[0].countryName == "Antarctica") {
                                findViewById<TextView>(R.id.tv_Language).setText("Penguin")
                                findViewById<TextView>(R.id.tv_Currency).setText("1 dollar = 1 frozen dollar")


                            } else {
                               var countryName = addrList[0].countryName
                                findViewById<TextView>(R.id.tv_Country).text = countryName

                                getCountryCode(countryCode) { country ->
                                    var language = country.language
                                    findViewById<TextView>(R.id.tv_Language).text = language

                                    convertCurrency(
                                        "USD",
                                        country.currencyBase,
                                        "1.00",
                                        country.currencySymbol
                                    ) { conversion ->
                                        runOnUiThread {
                                            findViewById<TextView>(R.id.tv_Currency).text =
                                                conversion

                                        }
                                        viewModel.updateVM(countryName,language, conversion)

                                    }
                                }
                            }
                        }
                    } catch (e: IOException) {
                        runOnUiThread {
                            Toast.makeText(applicationContext, "Unable connect to Geocoder", Toast.LENGTH_LONG).show()
                        }
                    }
                } ?: Toast.makeText(applicationContext, "Location not found.", Toast.LENGTH_SHORT).show()
            }
        })

        findViewById<TextView>(R.id.tv_Date).setOnClickListener {
            showDateRangePicker()
        }


        findViewById<Button>(R.id.bBack).setOnClickListener {
            startActivity(Intent(this,MainActivity::class.java))
        }


        findViewById<Button>(R.id.bCncyConv).setOnClickListener {
            startActivity(Intent(this,CurrencyConverter::class.java))
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
    private fun getCountryCode(countryCode: String, callback: (country) -> Unit) {
        val request = Request.Builder()
            .url("https://restcountries.com/v3.1/alpha/$countryCode?fields=currencies,languages")
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }
            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    throw IOException("Unexpected code $response")
                }

                // Parse the JSON response as an array
                val jsonResponse = JSONObject(response.body?.string())
                val currencies = jsonResponse.getJSONObject("currencies")
                val languages = jsonResponse.getJSONObject("languages")

                val currencyCode = currencies.keys().next()
                val currencyObject = currencies.getJSONObject(currencyCode)
                val currencyName = currencyObject.getString("name")
                val currencySymbol = currencyObject.getString("symbol")

                val languageName = languages.getString(languages.keys().next())
                val countryInfo = country(currencyName,currencyCode,currencySymbol,languageName)
                if (countryInfo != null) {
                    callback(countryInfo)
                }

            }

        })
    }
    private fun convertCurrency(
        from: String,
        to: String,
        amount: String,
        symbol: String,
        callback: (String) -> Unit
    ) {
        val request = Request.Builder()
            .url("https://currency-conversion-and-exchange-rates.p.rapidapi.com/convert?from=$from&to=$to&amount=$amount")
            .get()
            .addHeader("X-RapidAPI-Key", "c14eeaac84msh30ee8af59f8edb0p16c902jsn2912c477cc80")
            .addHeader("X-RapidAPI-Host", "currency-conversion-and-exchange-rates.p.rapidapi.com")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                callback.invoke("Failed to convert currency")
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    throw IOException("Unexpected code $response")
                } else {
                    val json = JSONObject(response.body?.string())
                    val result = json.getDouble("result")
                    val formatter = NumberFormat.getNumberInstance(Locale.US)
                    val formattedResult = formatter.format(result)
                    val conversion = "$amount $from = $formattedResult $to\nCurrency Symbol: $symbol"
                    callback.invoke(conversion)
                }
            }
        })
    }
}

