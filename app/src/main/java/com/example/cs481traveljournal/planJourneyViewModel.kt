package com.example.cs481traveljournal

import androidx.lifecycle.ViewModel

class planJourneyViewModel: ViewModel() {
    var countryName = ""
    var conversion = ""
    var language = ""
    fun updateVM(countryName: String,language: String, conversion: String ){
        this.countryName = countryName
        this.language = language
        this. conversion = conversion

    }
}