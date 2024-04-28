package com.example.cs481traveljournal

import MapFragment
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.libraries.places.widget.AutocompleteSupportFragment //1
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener


class MainActivity : AppCompatActivity() {
    //private lateinit var autocompleteFragment: AutocompleteSupportFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        permissionRequest()
        /*
        Places.initialize(applicationContext,getString(R.string.google_api_key)) //2
        autocompleteFragment = supportFragmentManager.findFragmentById(R.id.autocomplete_fragment)
                as AutocompleteSupportFragment
        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.ADDRESS, Place.Field.LAT_LNG))
        autocompleteFragment.setOnPlaceSelectedListener(object :PlaceSelectionListener{
            override fun onError(p0: Status) {
                Toast.makeText(this@MainActivity, "Error while searching", Toast.LENGTH_SHORT).show()
            }

            override fun onPlaceSelected(place: Place) {
                //val add = place.address
                //val id = place.id
                val latLng = place.latLng
            }

        })
         */

        // Get the SupportMapFragment and request notification when the map is ready to be used.
        supportFragmentManager.beginTransaction()
            .replace(R.id.cMap, MapFragment())
            .commit()
        findViewById<BottomNavigationView>(R.id.nHome).setOnItemSelectedListener {item ->
            when(item.itemId){
                R.id.ic_new_journey ->{
                    startActivity(Intent(this,plan_journey_activity::class.java))
                    true
                }
                else ->
                    true
            }
        }

    }

    /*
    private fun zoomOnMap(latLng: LatLng){
        val newLatLngZoom = CameraUpdateFactory.newLatLngZoom(latLng, 12f)
        googleMap?.animateCamera(newLatLngZoom)
    }
     */

    private fun permissionRequest(){
        var permissionList = mutableListOf<String>()
        if(!(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)){
            permissionList.add(android.Manifest.permission.ACCESS_COARSE_LOCATION)
        }
        if(!(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)){
            permissionList.add(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if(!(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED)){
            permissionList.add(android.Manifest.permission.INTERNET)
        }
        if(!(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)){
            permissionList.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if(permissionList.isNotEmpty()){
            ActivityCompat.requestPermissions(this,permissionList.toTypedArray(),100)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            100->{
                for(index in grantResults.indices){
                    if(grantResults[index] == PackageManager.PERMISSION_GRANTED){
                        Log.d("cs481traveljournal","Your ${permissions[index]} successfully granted")
                    }
                }
            }
        }
    }

}
