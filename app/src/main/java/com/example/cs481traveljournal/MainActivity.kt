package com.example.cs481traveljournal

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        permissionRequest()



        // Get the SupportMapFragment and request notification when the map is ready to be used.
        supportFragmentManager.beginTransaction()
            .replace(R.id.cMap, MapFragment())
            .commit()

    }
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
