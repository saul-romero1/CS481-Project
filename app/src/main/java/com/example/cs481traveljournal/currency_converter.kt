package com.example.cs481traveljournal
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.text.NumberFormat
import java.util.Locale
class CountryInfo {

    private val client = OkHttpClient()

    fun getCountryCode(country: String, callback: (String) -> Unit) {
        val request = Request.Builder()
            .url("https://country-information-and-conversion-api1.p.rapidapi.com/api/v1/country/name/United%20States")
            .get()
            .addHeader("X-RapidAPI-Key", "c14eeaac84msh30ee8af59f8edb0p16c902jsn2912c477cc80")
            .addHeader("X-RapidAPI-Host", "country-information-and-conversion-api1.p.rapidapi.com")
            .build()

        val response = client.newCall(request).execute()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                callback.invoke("Failed to get country code")
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    throw IOException("Unexpected code from getCountryCode $response")
                } else {
                    val json = JSONObject(response.body?.string())
                    val currency = json.getString("currency")

                }
            }
        })
    }

    fun convertCurrency(
        from: String,
        to: String,
        amount: String,
        callback: (String) -> Unit
    ) {
        getCountryCode(to) { currency ->
            val request = Request.Builder()
                .url("https://currency-conversion-and-exchange-rates.p.rapidapi.com/convert?from=USD&to=EUR&amount=1.00")
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
                        val conversion = "$amount $from = $formattedResult $currency"
                        callback.invoke(conversion)
                    }
                }
            })
        }
    }

}
















