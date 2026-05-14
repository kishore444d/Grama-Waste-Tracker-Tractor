package com.example.tractor_app

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log

import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {

    // Firebase Database
    private val database = FirebaseDatabase.getInstance(
        "https://gramawastetracker-a717f-default-rtdb.asia-southeast1.firebasedatabase.app"
    )

    // GPS Client
    private lateinit var fusedLocationClient:
            FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        // Initialize GPS
        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(this)

        // Permission Check
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                1
            )

            return
        }

        // Start Live GPS Upload
        startLiveLocationUpdates()
    }

    @SuppressLint("MissingPermission")
    private fun startLiveLocationUpdates() {

        val locationRequest =
            LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                5000
            ).build()

        val locationCallback =
            object : LocationCallback() {

                override fun onLocationResult(
                    locationResult: LocationResult
                ) {

                    for (location in locationResult.locations) {

                        val latitude =
                            location.latitude

                        val longitude =
                            location.longitude

                        Log.d(
                            "GPS",
                            "Location: " +
                                    "$latitude , $longitude"
                        )

                        // Upload to Firebase
                        val locationRef =
                            database.getReference(
                                "vehicles/tractor1"
                            )

                        locationRef.setValue(
                            mapOf(
                                "latitude" to latitude,
                                "longitude" to longitude
                            )
                        )
                    }
                }
            }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            mainLooper
        )
    }
}