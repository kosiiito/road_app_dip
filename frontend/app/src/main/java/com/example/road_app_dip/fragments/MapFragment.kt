package com.example.road_app_dip.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.road_app_dip.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapFragment : Fragment(R.layout.fragment_map), OnMapReadyCallback {

    private var googleMap: GoogleMap? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.mapFragment) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        val sofia = LatLng(42.6977, 23.3242)
        googleMap?.apply {
            addMarker(MarkerOptions().position(sofia).title("София"))
            moveCamera(CameraUpdateFactory.newLatLngZoom(sofia, 12f))
        }
    }
}
