package com.example.cs481traveljournal

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.text.NumberFormat
import java.util.Locale

class CurrencyConverter : AppCompatActivity() {//create variables
    private lateinit var fromCurrency: Spinner
    private lateinit var toCurrency: Spinner
    private lateinit var amount: EditText
    private lateinit var convertButton: Button
    private lateinit var resultText: TextView
    private lateinit var dateText: TextView
    private lateinit var rateText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_currency_converter)

        //assign variables
        fromCurrency = findViewById(R.id.fromCurrency)
        toCurrency = findViewById(R.id.toCurrency)
        amount = findViewById(R.id.amount)
        convertButton = findViewById(R.id.convertButton)
        resultText = findViewById(R.id.resultText)
        dateText = findViewById(R.id.dateText)
        rateText = findViewById(R.id.rateText)

        //currency array
        val currencies = arrayOf(
            "ANG - Netherlands Antillean Guilder",
            "SVC - Salvadoran Colón",
            "CAD - Canadian Dollar",
            "XCD - East Caribbean Dollar",
            "MVR - Maldivian Rufiyaa",
            "HRK - Croatian Kuna",
            "AUD - Australian Dollar",
            "MWK - Malawian Kwacha",
            "XAG - Silver (troy ounce)",
            "MAD - Moroccan Dirham",
            "PHP - Philippine Peso",
            "NAD - Namibian Dollar",
            "GNF - Guinean Franc",
            "KES - Kenyan Shilling",
            "MZN - Mozambican Metical",
            "BTN - Bhutanese Ngultrum",
            "MGA - Malagasy Ariary",
            "AZN - Azerbaijani Manat",
            "XAU - Gold (troy ounce)",
            "RON - Romanian Leu",
            "CHF - Swiss Franc",
            "EGP - Egyptian Pound",
            "BSD - Bahamian Dollar",
            "TWD - New Taiwan Dollar",
            "GGP - Guernsey Pound",
            "LVL - Latvian Lats",
            "MMK - Myanma Kyat",
            "WST - Samoan Tala",
            "ILS - Israeli New Sheqel",
            "BHD - Bahraini Dinar",
            "GBP - British Pound Sterling",
            "TZS - Tanzanian Shilling",
            "SDG - South Sudanese Pound",
            "LAK - Laotian Kip",
            "DJF - Djiboutian Franc",
            "BYN - New Belarusian Ruble",
            "LBP - Lebanese Pound",
            "RWF - Rwandan Franc",
            "PEN - Peruvian Nuevo Sol",
            "EUR - Euro",
            "ZMK - Zambian Kwacha (pre-2013)",
            "RSD - Serbian Dinar",
            "INR - Indian Rupee",
            "MUR - Mauritian Rupee",
            "BWP - Botswanan Pula",
            "GEL - Georgian Lari",
            "KMF - Comorian Franc",
            "UZS - Uzbekistan Som",
            "RUB - Russian Ruble",
            "CUC - Cuban Convertible Peso",
            "BGN - Bulgarian Lev",
            "JOD - Jordanian Dinar",
            "NGN - Nigerian Naira",
            "BDT - Bangladeshi Taka",
            "PKR - Pakistani Rupee",
            "BRL - Brazilian Real",
            "KZT - Kazakhstani Tenge",
            "CVE - Cape Verdean Escudo",
            "HNL - Honduran Lempira",
            "NZD - New Zealand Dollar",
            "ERN - Eritrean Nakfa",
            "NPR - Nepalese Rupee",
            "ZMW - Zambian Kwacha",
            "FKP - Falkland Islands Pound",
            "DZD - Algerian Dinar",
            "JMD - Jamaican Dollar",
            "CRC - Costa Rican Colón",
            "GMD - Gambian Dalasi",
            "PLN - Polish Zloty",
            "AMD - Armenian Dram",
            "BMD - Bermudan Dollar",
            "BZD - Belize Dollar",
            "BBD - Barbadian Dollar",
            "SBD - Solomon Islands Dollar",
            "IDR - Indonesian Rupiah",
            "ALL - Albanian Lek",
            "IQD - Iraqi Dinar",
            "BIF - Burundian Franc",
            "HKD - Hong Kong Dollar",
            "GIP - Gibraltar Pound",
            "BAM - Bosnia-Herzegovina Convertible Mark",
            "LKR - Sri Lankan Rupee",
            "QAR - Qatari Rial",
            "SAR - Saudi Riyal",
            "TOP - Tongan Paʻanga",
            "SEK - Swedish Krona",
            "ZAR - South African Rand",
            "ARS - Argentine Peso",
            "MYR - Malaysian Ringgit",
            "BYR - Belarusian Ruble",
            "KPW - North Korean Won",
            "CZK - Czech Republic Koruna",
            "STD - São Tomé and Príncipe Dobra",
            "BTC - Bitcoin",
            "ZWL - Zimbabwean Dollar",
            "LSL - Lesotho Loti",
            "COP - Colombian Peso",
            "PAB - Panamanian Balboa",
            "IRR - Iranian Rial",
            "CNH - Chinese Yuan Offshore",
            "NOK - Norwegian Krone",
            "XPF - CFP Franc",
            "XOF - CFA Franc BCEAO",
            "XDR - Special Drawing Rights",
            "OMR - Omani Rial",
            "CNY - Chinese Yuan",
            "NIO - Nicaraguan Córdoba",
            "AOA - Angolan Kwanza",
            "SCR - Seychellois Rupee",
            "MOP - Macanese Pataca",
            "ISK - Icelandic Króna",
            "VND - Vietnamese Dong",
            "VES - Sovereign Bolivar",
            "USD - United States Dollar",
            "UYU - Uruguayan Peso",
            "VEF - Venezuelan Bolívar Fuerte",
            "MRU - Mauritanian Ouguiya",
            "UGX - Ugandan Shilling",
            "DOP - Dominican Peso",
            "UAH - Ukrainian Hryvnia",
            "BOB - Bolivian Boliviano",
            "TTD - Trinidad and Tobago Dollar",
            "KGS - Kyrgystani Som",
            "TND - Tunisian Dinar",
            "SGD - Singapore Dollar",
            "TMT - Turkmenistani Manat",
            "GHS - Ghanaian Cedi",
            "TJS - Tajikistani Somoni",
            "KHR - Cambodian Riel",
            "ETB - Ethiopian Birr",
            "PGK - Papua New Guinean Kina",
            "THB - Thai Baht",
            "AED - United Arab Emirates Dirham",
            "GTQ - Guatemalan Quetzal",
            "LRD - Liberian Dollar",
            "SYP - Syrian Pound",
            "KYD - Cayman Islands Dollar",
            "SRD - Surinamese Dollar",
            "HTG - Haitian Gourde",
            "LYD - Libyan Dinar",
            "SLL - Sierra Leonean Leone",
            "SLE - Sierra Leonean Leone",
            "SHP - Saint Helena Pound",
            "IMP - Manx pound",
            "FJD - Fijian Dollar",
            "PYG - Paraguayan Guarani",
            "KRW - South Korean Won",
            "SZL - Swazi Lilangeni",
            "GYD - Guyanaese Dollar",
            "MDL - Moldovan Leu",
            "MXN - Mexican Peso",
            "CLP - Chilean Peso",
            "LTL - Lithuanian Litas",
            "SOS - Somali Shilling",
            "MNT - Mongolian Tugrik",
            "AFN - Afghan Afghani",
            "CUP - Cuban Peso",
            "CLF - Chilean Unit of Account (UF)",
            "JPY - Japanese Yen",
            "TRY - Turkish Lira",
            "YER - Yemeni Rial",
            "HUF - Hungarian Forint",
            "BND - Brunei Dollar",
            "JEP - Jersey Pound",
            "MKD - Macedonian Denar",
            "AWG - Aruban Florin",
            "CDF - Congolese Franc",
            "VUV - Vanuatu Vatu",
            "XAF - CFA Franc BEAC",
            "KWD - Kuwaiti Dinar",
            "DKK - Danish Krone"
        )
        currencies.sort()   //sort alphabetically

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, currencies)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        fromCurrency.adapter = adapter
        toCurrency.adapter = adapter
        //set defaults
        fromCurrency.setSelection(adapter.getPosition("USD - United States Dollar"))
        toCurrency.setSelection(adapter.getPosition("EUR - Euro"))
        //convert btn
        convertButton.setOnClickListener {
            //format to/from for use w api
            val from = fromCurrency.selectedItem.toString().split(" ")[0]
            val to = toCurrency.selectedItem.toString().split(" ")[0]
            val amt = amount.text.toString()

            val client = OkHttpClient()
            //call api
            val request = Request.Builder()
                .url("https://currency-conversion-and-exchange-rates.p.rapidapi.com/convert?from=$from&to=$to&amount=$amt")
                .get()
                .addHeader("X-RapidAPI-Key", "c14eeaac84msh30ee8af59f8edb0p16c902jsn2912c477cc80")
                .addHeader("X-RapidAPI-Host", "currency-conversion-and-exchange-rates.p.rapidapi.com")
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }

                override fun onResponse(call: Call, response: Response) {
                    if (!response.isSuccessful) {
                        throw IOException("Unexpected code $response")
                    } else {
                        val responseData = response.body?.string()
                        val json = JSONObject(responseData)

                        val date = json.getString("date")
                        val result = json.getDouble("result")
                        val rate = json.getJSONObject("info").getDouble("rate")
                        //format result
                        val formatter = NumberFormat.getNumberInstance(Locale.US)
                        val formattedResult = formatter.format(result)
                        val formattedRate = formatter.format(rate)

                        runOnUiThread {
                            resultText.text = "Result: $formattedResult"
                            rateText.text = "Rate: $formattedRate"
                            dateText.text = "Date: $date"
                        }
                    }
                }
            })
        }
    }
}