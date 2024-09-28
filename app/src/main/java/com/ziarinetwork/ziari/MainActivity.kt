package com.ziarinetwork.ziari

import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
import com.ziarinetwork.ziari.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding : ActivityMainBinding
    private lateinit var googleMap: GoogleMap
    private lateinit var currentLocation: Location
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    private val locationPermissionCode = 101
    private var density = 0f

    private lateinit var adapter: MyAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var imagesList: ArrayList<PhotoModel>

    private lateinit var photoModel: PhotoModel
    private lateinit var descr: Array<String>
    private lateinit var photo: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dataInitialize()

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView = findViewById(R.id.markersRecyclerView)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        adapter = MyAdapter(imagesList)
        recyclerView.adapter = adapter

        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.markersInfo))

        val display = windowManager.defaultDisplay
        val outMetrics = DisplayMetrics()
        display.getMetrics(outMetrics)

        density = resources.displayMetrics.density
        val dpHeight = outMetrics.heightPixels / density
        val dpWidth = outMetrics.widthPixels / density

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        getCurrentLocationUser()

        bottomSheetBehavior.apply {
            peekHeight = 120
            maxHeight = (outMetrics.heightPixels/1.7).toInt()
            this.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.bottomNavigation.setOnNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.home -> {
                    binding.mapContainer.visibility = View.VISIBLE

                }
                R.id.profile -> {
                    binding.mapContainer.visibility = View.GONE
                    replaceFragment(ProfileFragment())
                }
                R.id.groups -> {
                    binding.mapContainer.visibility = View.GONE
                    replaceFragment(GroupsFragment())
                }
                R.id.message -> {
                    binding.mapContainer.visibility = View.GONE
                    replaceFragment(MessageFragment())
                }
                else -> false
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentContainer, fragment)
            commit()
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
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
//            LatLng(currentLocation.latitude, currentLocation.longitude),
            LatLng(41.715681, 44.785689),
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
        bottomSheetBehavior.isHideable = true
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        Log.i("POSITION", "Zoom level changed to: $newPosition")
    }

    private fun onMarkerClicked(marker: Marker) {
        // Show BottomSheetDialogFragment with the image URL
        val imageUrl = marker.tag as? String

//        val bottomSheetFragment = ImageBottomSheetFragment.newInstance(imageUrl.orEmpty())
//        bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)

        imageUrl?.let {
            val bottomSheetFragment = ImageBottomSheetFragment.newInstance(it)
            bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
        }
    }

    private fun dataInitialize(){
        imagesList = arrayListOf<PhotoModel>()
        descr = arrayOf("Tbilisi", "Batumi", "New York", "Paris", "London")
        photo = arrayOf("https://thumbs.dreamstime.com/b/bamboo-hanging-bridge-over-sea-to-tropical-island-pedestrian-remote-desert-beautiful-landscape-travel-lifestyle-wild-nature-82365255.jpg",
            "https://i.insider.com/5f5a895be6ff30001d4e82b3?width=800&format=jpeg&auto=webp",
            "https://images.pexels.com/photos/3278215/pexels-photo-3278215.jpeg?cs=srgb&dl=pexels-freestockpro-3278215.jpg&fm=jpg",
            "https://i.insider.com/5f5a895be6ff30001d4e82b3?width=800&format=jpeg&auto=webp",
            "https://thumbs.dreamstime.com/b/exotic-tropical-resort-jetty-near-cancun-mexico-travel-vacations-concept-tourism-87825663.jpg")

        for (i in descr.indices){
            photoModel = PhotoModel(descr[i], photo[i])
            imagesList.add(photoModel)
        }
    }

}