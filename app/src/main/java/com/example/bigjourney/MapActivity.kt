package com.example.bigjourney

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import org.maplibre.android.MapLibre
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.location.LocationComponentActivationOptions
import org.maplibre.android.location.modes.CameraMode
import org.maplibre.android.location.modes.RenderMode
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.MapLibreMap
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import android.util.Log
import android.widget.Button

import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import android.location.Geocoder
import android.location.Address
import java.util.Locale


class MapActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private lateinit var mapLibreMap: MapLibreMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Init MapLibre
        MapLibre.getInstance(this)

        // Init layout view
        val inflater = LayoutInflater.from(this)
        val rootView = inflater.inflate(R.layout.activity_map, null)
        setContentView(rootView)

        // Init the MapView
        mapView = rootView.findViewById(R.id.mapView)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        mapView.getMapAsync { map ->
            mapLibreMap = map
            map.setStyle("https://api.maptiler.com/maps/streets/style.json?key=yWJKPGOBYtl9xXIPD3Bk") { // Χρησιμοποιούμε ένα ελαφρύ στυλ
                enableLocationComponent()
            }

            setupSearch()

            // Προσθήκη του κουμπιού για επιστροφή στην τοποθεσία του χρήστη
            val buttonLocate = findViewById<Button>(R.id.buttonLocate)
            buttonLocate.setOnClickListener {
                returnToUserLocation()
            }
        }
    }

    private fun setupSearch() {
        val searchView = findViewById<SearchView>(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    searchLocation(query)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }

    private fun searchLocation(query: String) {
        val url = "https://api.maptiler.com/geocoding/$query.json?key=yWJKPGOBYtl9xXIPD3Bk"

        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("MapSearch", "Error fetching location", e)
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.string()?.let { json ->
                    val jsonObject = JSONObject(json)
                    val features = jsonObject.getJSONArray("features")

                    if (features.length() > 0) {
                        val firstResult = features.getJSONObject(0)
                        val coordinates = firstResult.getJSONObject("geometry").getJSONArray("coordinates")
                        val lon = coordinates.getDouble(0)
                        val lat = coordinates.getDouble(1)

                        runOnUiThread {
                            moveToLocation(lat, lon)
                        }
                    }
                }
            }
        })
    }



    private fun moveToLocation(lat: Double, lon: Double) {
        val position = CameraPosition.Builder()
            .target(LatLng(lat, lon))
            .zoom(12.0)
            .build()

        mapLibreMap.cameraPosition = position
        // Επιστρέφουμε τις συντεταγμένες στην Activity που κάλεσε τον χάρτη
        val resultIntent = Intent()
        resultIntent.putExtra("latitude", lat)
        resultIntent.putExtra("longitude", lon)
        setResult(RESULT_OK, resultIntent)
        finish()
    }

    private fun enableLocationComponent() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            return
        }

        val locationComponent = mapLibreMap.locationComponent
        val locationComponentActivationOptions = LocationComponentActivationOptions.builder(this, mapLibreMap.style!!)
            .useDefaultLocationEngine(true)
            .build()

        locationComponent.activateLocationComponent(locationComponentActivationOptions)
        locationComponent.isLocationComponentEnabled = true
        locationComponent.cameraMode = CameraMode.TRACKING
        locationComponent.renderMode = RenderMode.NORMAL

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                val latLng = LatLng(it.latitude, it.longitude)
                mapLibreMap.cameraPosition = CameraPosition.Builder()
                    .target(latLng)
                    .zoom(14.0)
                    .build()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            enableLocationComponent()
        }
    }

    private fun returnToUserLocation() {
        val locationComponent = mapLibreMap.locationComponent
        if (locationComponent.isLocationComponentEnabled) {
            val currentLocation = locationComponent.lastKnownLocation
            currentLocation?.let {
                val position = CameraPosition.Builder()
                    .target(LatLng(it.latitude, it.longitude))
                    .zoom(14.0)
                    .build()
                mapLibreMap.cameraPosition = position
            }
        }
    }


    override fun onStart() { super.onStart(); mapView.onStart() }
    override fun onResume() { super.onResume(); mapView.onResume() }
    override fun onPause() { super.onPause(); mapView.onPause() }
    override fun onStop() { super.onStop(); mapView.onStop() }
    override fun onLowMemory() { super.onLowMemory(); mapView.onLowMemory() }
    override fun onDestroy() { super.onDestroy(); mapView.onDestroy() }
    override fun onSaveInstanceState(outState: Bundle) { super.onSaveInstanceState(outState); mapView.onSaveInstanceState(outState) }
}

