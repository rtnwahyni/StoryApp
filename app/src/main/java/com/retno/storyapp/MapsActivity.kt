package com.retno.storyapp

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.retno.storyapp.api.Injection
import com.retno.storyapp.databinding.ActivityMapsBinding
import com.retno.storyapp.viemodel.StoryViewModel

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var storyViewModel: StoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val storyRepository = Injection.provideRepository(this)
        val viewModelFactory = StoryViewModelFactory(storyRepository)
        storyViewModel = ViewModelProvider(this, viewModelFactory)[StoryViewModel::class.java]

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        // Add a marker in Sydney and move the camera
        //val sydney = LatLng(-34.0, 151.0)
        //mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

        val dicodingSpace = LatLng(-6.8957643, 107.6338462)
        mMap.addMarker(
            MarkerOptions()
                .position(dicodingSpace)
                .title("Dicoding Space")
                .snippet("Batik Kumeli No.50")
        )
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(dicodingSpace, 15f))

        getMyLocation()
        addManyMarker()
    }

    private val boundsBuilder = LatLngBounds.Builder()
    data class TourismPlace(
        val name: String,
        val latitude: Double,
        val longitude: Double
    )

    //private fun addManyMarker() {
       // storyViewModel.getStoriesWithLocation()
        //storyViewModel.stories.observe(this){ stories ->
//            if stories.isNotEmpty()
//            stories.forEach()
//
//        }
//        val bounds: LatLngBounds = boundsBuilder.build()
//        mMap.animateCamera(
//            CameraUpdateFactory.newLatLngBounds(
//                bounds,
//                resources.displayMetrics.widthPixels,
//                resources.displayMetrics.heightPixels,
//                300
//            )
//        )
//
//    }
    private fun addManyMarker() {
        storyViewModel.getStoriesWithLocation()
        storyViewModel.stories.observe(this){ stories ->
            if (stories.isNotEmpty()) {
                stories.forEach { story ->
                    location.add(TourismPlace( )
                }

                location.forEach {loc ->
                    val latLng = LatLng(loc.latitude!!, loc.longitude!!)
                    mMap.addMarker(MarkerOptions().position(latlng).title(loc.name)
                            boundsBuilder.include(latlng)
                }
            }
        }
    }
    
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }
    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.map_options, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.normal_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                true
            }
            R.id.satellite_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
                true
            }
            R.id.terrain_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
                true
            }
            R.id.hybrid_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }
}