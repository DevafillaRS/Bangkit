package com.dicoding.stroyapp.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.dicoding.stroyapp.R
import com.dicoding.stroyapp.databinding.ActivityMapsBinding
import com.dicoding.stroyapp.factory.ViewModelFactory
import com.dicoding.stroyapp.response.ListStoryItem
import com.dicoding.stroyapp.viewmodel.MapsViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private lateinit var viewModelFactory: ViewModelFactory
    private val mapsViewModel: MapsViewModel by viewModels { viewModelFactory }

    private val boundsBuilder = LatLngBounds.Builder()

    private var storiesWithLocation: List<ListStoryItem> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        setupViewModel()

        mapsViewModel.user.observe(this) { mapLoc ->
            if (mapLoc != null) {
                storiesWithLocation = mapLoc
                mapFragment.getMapAsync(this)
            }
        }
    }

    private fun setupViewModel() {
        viewModelFactory = ViewModelFactory.getInstance(this)

        mapsViewModel.getToken().observe(this) {
            if (it != "") {
                mapsViewModel.getStoryWithLocation(it)
            } else {
                startActivity(Intent(this@MapsActivity, WelcomeActivity::class.java))
                finish()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        getMyLocation()
        setupMarkerFromStory()
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
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun setupMarkerFromStory() {
        if (storiesWithLocation.isEmpty()) return

        storiesWithLocation.forEach { loc ->
            val location = LatLng(loc.lat, loc.lon)
            val address = getAddress(location)
            val marker = mMap.addMarker(
                MarkerOptions()
                    .position(location)
                    .title(address)
            )
            boundsBuilder.include(location)
            marker?.showInfoWindow()
        }
        val bounds: LatLngBounds = boundsBuilder.build()
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 10))
    }

    private fun getAddress(latLng: LatLng): String {
        return try {
            val geocoder = Geocoder(this)
            val address = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            if (address?.isEmpty() == true) {
                getString(R.string.kosong)
            } else {
                address!![0].getAddressLine(0)
            }
        } catch (e: Exception) {
            getString(R.string.kosong)
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