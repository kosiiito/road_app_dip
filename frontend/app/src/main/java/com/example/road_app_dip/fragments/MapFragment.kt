package com.example.road_app_dip

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.road_app_dip.network.ApiService
import com.example.road_app_dip.network.ApiInterface
import com.example.road_app_dip.models.Location
import com.google.android.gms.awareness.snapshot.LocationResponse
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.Request
import okhttp3.OkHttpClient
import retrofit2.Response

class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private val api: ApiInterface = ApiService.create()
    private val markers = mutableListOf<Marker>()

    private val bearerToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjY3OTY3OTIzZGZkYjgzNWMzNmI3Zjk4OSIsImlhdCI6MTczODkxNjIyNiwiZXhwIjoxNzM4OTE5ODI2fQ.KcnIW4Q1DK4bZE2p3ZARY4EAGiNpG1M2YoCrniZ2UbA"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_map, container, false)

        mapView = view.findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        return view
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        loadExistingLocations()

        googleMap.setOnMapClickListener { latLng ->
            showAddMarkerDialog(latLng)
        }

        val defaultLocation = LatLng(42.6977, 23.3242)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 10f))
    }

    private fun showAddMarkerDialog(latLng: LatLng) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Добавяне на локация")

        val input = EditText(requireContext())
        input.hint = "Описание на проблема"
        builder.setView(input)

        builder.setPositiveButton("Запази") { _, _ ->
            val description = input.text.toString().trim()
            if (description.isNotEmpty()) {
                saveLocationToServer(latLng, description)
            } else {
                Toast.makeText(requireContext(), "Описание не може да е празно!", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("Отказ") { dialog, _ -> dialog.cancel() }
        builder.show()

        input.requestFocus()
    }

    private fun saveLocationToServer(latLng: LatLng, description: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Създаване на Location обект
                val location = Location(
                    userId = "67967923dfdb835c36b7f989",  // Или можеш да вземеш стойност от потребител
                    latitude = latLng.latitude,
                    longitude = latLng.longitude,
                    description = description
                )

                // Извикваме метода от ApiInterface за добавяне на локацията
                val response = api.addLocation(location)

                if (response.isSuccessful) {
                    requireActivity().runOnUiThread {
                        addMarkerToMap(latLng, description)
                        Toast.makeText(requireContext(), "Локацията е добавена!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    requireActivity().runOnUiThread {
                        Toast.makeText(requireContext(), "Грешка при добавянето!", Toast.LENGTH_SHORT).show()
                    }
                    Log.e("API Error", "Response code: ${response.code()}")
                }
            } catch (e: Exception) {
                requireActivity().runOnUiThread {
                    Toast.makeText(requireContext(), "Грешка: ${e.message}", Toast.LENGTH_SHORT).show()
                }
                Log.e("API Error", "Exception: ${e.message}")
            }
        }
    }


    private fun loadExistingLocations() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = api.getLocations() // Заявка към API

                if (response.isSuccessful) {
                    val body = response.body() // Това ще е тип LocationResponse

                    if (body != null) {
                        val locations = body.locations // Списък с локации

                        requireActivity().runOnUiThread {
                            if (locations.isEmpty()) {
                                Toast.makeText(requireContext(), "Няма налични локации", Toast.LENGTH_SHORT).show()
                            }
                            for (location in locations) {
                                val latLng = LatLng(location.latitude, location.longitude)
                                addMarkerToMap(latLng, location.description)
                            }
                        }
                    } else {
                        requireActivity().runOnUiThread {
                            Toast.makeText(requireContext(), "Грешка в отговора на сървъра", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    requireActivity().runOnUiThread {
                        Toast.makeText(requireContext(), "Грешка при зареждане на локации: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                    Log.e("API Error", "Response code: ${response.code()}")
                }
            } catch (e: Exception) {
                requireActivity().runOnUiThread {
                    Toast.makeText(requireContext(), "Неуспешно зареждане на локации: ${e.message}", Toast.LENGTH_SHORT).show()
                }
                Log.e("API Error", "Exception: ${e.message}")
            }
        }
    }



    private fun addMarkerToMap(latLng: LatLng, description: String) {
        val marker = googleMap.addMarker(
            MarkerOptions().position(latLng).title(description)
        )
        marker?.let { markers.add(it) }
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }
}
