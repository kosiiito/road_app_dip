package com.example.road_app_dip

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.road_app_dip.network.ApiService
import com.example.road_app_dip.network.ApiInterface
import com.example.road_app_dip.models.Location
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


class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private val api: ApiInterface = ApiService.create()
    private val markers = mutableListOf<Marker>()

    private fun getToken(): String? {
        val sharedPreferences = requireContext().getSharedPreferences("AppPrefs", android.content.Context.MODE_PRIVATE)
        return sharedPreferences.getString("bearer_token", null)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_map, container, false)
        mapView = view.findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
        return view
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        loadExistingLocations()
        googleMap.setOnMapClickListener { latLng -> showAddMarkerDialog(latLng) }
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
        val token = getToken()
        if (token == null) {
            Toast.makeText(requireContext(), "Няма активен токен!", Toast.LENGTH_SHORT).show()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val location = Location("userId", latLng.latitude, latLng.longitude, description)
                val response = api.addLocation(location,"Bearer $token")
                requireActivity().runOnUiThread {
                    if (response.isSuccessful) {
                        addMarkerToMap(latLng, description)
                        Toast.makeText(requireContext(), "Локацията е добавена!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Грешка при добавянето! Код: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                requireActivity().runOnUiThread {
                    Toast.makeText(requireContext(), "Грешка: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun loadExistingLocations() {
        val token = getToken() ?: return
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = api.getLocations("Bearer $token")
                requireActivity().runOnUiThread {
                    if (response.isSuccessful) {
                        response.body()?.locations?.forEach {
                            addMarkerToMap(LatLng(it.latitude, it.longitude), it.description)
                        }
                    } else {
                        Toast.makeText(requireContext(), "Грешка при зареждане: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                requireActivity().runOnUiThread {
                    Toast.makeText(requireContext(), "Неуспешно зареждане: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun addMarkerToMap(latLng: LatLng, description: String) {
        googleMap.addMarker(MarkerOptions().position(latLng).title(description))?.let { markers.add(it) }
    }

    override fun onResume() { super.onResume(); mapView.onResume() }
    override fun onPause() { super.onPause(); mapView.onPause() }
    override fun onDestroy() { super.onDestroy(); mapView.onDestroy() }
}
