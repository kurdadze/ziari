package com.ziarinetwork.ziari

import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior


class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private lateinit var currentLocation: Location
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val locationPermissionCode = 101
    private var density = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val display = windowManager.defaultDisplay
        val outMetrics = DisplayMetrics()
        display.getMetrics(outMetrics)

        density = resources.displayMetrics.density
        val dpHeight = outMetrics.heightPixels / density
        val dpWidth = outMetrics.widthPixels / density

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        getCurrentLocationUser()

        BottomSheetBehavior.from(findViewById(R.id.markersInfo)).apply {
            peekHeight = 120
            this.state = BottomSheetBehavior.STATE_COLLAPSED
        }

    }

    private fun getCurrentLocationUser() {
        if(ActivityCompat.checkSelfPermission(
                this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), locationPermissionCode)
            return
        }
        fusedLocationProviderClient.lastLocation.addOnSuccessListener {
                location ->
            if(location != null) {
                currentLocation = location
                val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
                mapFragment.getMapAsync(this)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            locationPermissionCode -> if(grantResults.isNotEmpty() && grantResults[0] ==
                PackageManager.PERMISSION_GRANTED) {
                getCurrentLocationUser()
            }
        }
    }

    override fun onMapReady(gMap: GoogleMap) {
        googleMap = gMap

        val locations = listOf(
            LatLng(currentLocation.latitude, currentLocation.longitude),
            LatLng(41.713521, 44.782219),
            LatLng(40.748817, -73.985428),
            LatLng(48.858844, 2.294351),
            LatLng(51.507351, -0.127758)
        )

        val imageUrls = listOf(
            "https://thumbs.dreamstime.com/b/bamboo-hanging-bridge-over-sea-to-tropical-island-pedestrian-remote-desert-beautiful-landscape-travel-lifestyle-wild-nature-82365255.jpg",
            "https://i.insider.com/5f5a895be6ff30001d4e82b3?width=800&format=jpeg&auto=webp",
            "https://images.pexels.com/photos/3278215/pexels-photo-3278215.jpeg?cs=srgb&dl=pexels-freestockpro-3278215.jpg&fm=jpg",
            "https://i.insider.com/5f5a895be6ff30001d4e82b3?width=800&format=jpeg&auto=webp",
            "https://thumbs.dreamstime.com/b/exotic-tropical-resort-jetty-near-cancun-mexico-travel-vacations-concept-tourism-87825663.jpg"
        )

        var markersWidthHeight = 0

        markersWidthHeight = if(density < 3) 90 else 60

        for ((index, latLng) in locations.withIndex()) {
            val markerOptions = MarkerOptions().position(latLng)
            val marker = googleMap.addMarker(markerOptions)

            marker?.tag = imageUrls[index]

            marker?.loadCustomIcon(
                context = this,
                imageUrl = imageUrls[index],
                width = markersWidthHeight,
                height = markersWidthHeight
            )

            googleMap.setOnCameraIdleListener {
                val targetPosition = googleMap.cameraPosition.target
                onMapPositionChanged(targetPosition)
            }
        }

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locations[0], 6f))

        googleMap.setOnMarkerClickListener { marker ->
            onMarkerClicked(marker)
            true
        }
    }

    private fun onMapPositionChanged(newPosition: LatLng) {
        Log.i("POSITION", "Zoom level changed to: $newPosition")
    }

    private fun onMarkerClicked(marker: Marker) {
        // Show BottomSheetDialogFragment with the image URL
        val imageUrl = marker.tag as? String
    }

}