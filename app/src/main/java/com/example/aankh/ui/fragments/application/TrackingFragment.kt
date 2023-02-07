package com.example.aankh.ui.fragments.application

import android.annotation.SuppressLint

import android.content.Intent
import android.graphics.Color
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aankh.R
import com.example.aankh.adapters.CheckPointsAdapter
import com.example.aankh.databinding.FragmentTrackingBinding

import com.example.aankh.service.Tracker
import com.example.aankh.utils.Constants.ACTION_SERVICE_START
import com.example.aankh.utils.Constants.ACTION_SERVICE_STOP
import com.example.aankh.utils.Constants.BACKGROUND_PERMISSION_REQUEST_CODE
import com.example.aankh.utils.Constants.PERMISSION_LOCATION_REQUEST_CODE
import com.example.aankh.utils.Extension.hide
import com.example.aankh.utils.Extension.show
import com.example.aankh.utils.MapUtil.setCameraPosition
import com.example.aankh.utils.Permissions.hasBackgroundPermission
import com.example.aankh.utils.Permissions.hasLocationPermission
import com.example.aankh.utils.Permissions.requestBackgroundPermission
import com.example.aankh.utils.Permissions.requestLocationPermission
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import pub.devrel.easypermissions.AppSettingsDialog

import pub.devrel.easypermissions.EasyPermissions

class TrackingFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener,
    EasyPermissions.PermissionCallbacks, GoogleMap.OnMarkerClickListener {

    private var _binding: FragmentTrackingBinding? = null
    private val binding get() = _binding!!
    private lateinit var map: GoogleMap
    private var locationList = mutableListOf<LatLng>()
    private var starTime = 0L
    private var stopTime = 0L
    private var started = false
    private var polylineList = mutableListOf<Polyline>()
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var markerList = mutableListOf<Marker>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTrackingBinding.inflate(inflater, container, false)

//        TODO make a get request for today's check points


////        TODO get location permissions before moving to fragment
//        fusedLocationProviderClient =
//            LocationServices.getFusedLocationProviderClient(requireActivity())

        binding.startButtonMapsActivity.setOnClickListener {
            onStartButtonClicked()
        }

        binding.stopButtonMapsActivity.setOnClickListener {
            onStopButtonClicked()
        }

//TODO here we have to change the recycler view by making a get request for day checkpoints
        binding.checkPointsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.checkPointsRecyclerView.adapter = CheckPointsAdapter()
        return binding.root
    }

    private fun onStartButtonClicked() {

        if (hasBackgroundPermission(requireContext()) && hasLocationPermission(requireContext())) {
            onStartCommands()
        } else if (hasBackgroundPermission(requireContext())) {
            requestLocationPermission(this)
        } else {
            requestBackgroundPermission(this)
            requestLocationPermission(this)
        }

    }

    @SuppressLint("MissingPermission")
    private fun onStartCommands() {
        binding.startButtonMapsActivity.hide()
        binding.stopButtonMapsActivity.show()
        map.isMyLocationEnabled = true
        sendActionCommandtoService(ACTION_SERVICE_START)
    }

    private fun onStopButtonClicked() {
        AlertDialog.Builder(requireContext()).setTitle("Stop Tracking")
            .setMessage("We will send your today's report to authorities once you stop tracking your activity. Are you sure that you want to stop tracking?")
            .setPositiveButton("No") { dialog, _ ->
                dialog.dismiss()
            }.setNegativeButton("Yes") { _, _ ->
                binding.stopButtonMapsActivity.hide()
                binding.startButtonMapsActivity.show()
                sendActionCommandtoService(ACTION_SERVICE_STOP)
            }.show()
    }


    private fun sendActionCommandtoService(action: String) {
        Intent(requireContext(), Tracker::class.java).apply {
            this.action = action
            requireContext().startService(this)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        setMainCameraPosition()
        map.setOnMyLocationButtonClickListener(this)
        map.setOnMarkerClickListener(this)
        observeTrackerService()
        if (started) {
            binding.startButtonMapsActivity.hide()
            binding.stopButtonMapsActivity.show()
            map.isMyLocationEnabled = true
        }

    }

    private fun setMainCameraPosition() {
        map.animateCamera(
            CameraUpdateFactory.newCameraPosition(
                CameraPosition.Builder().target(LatLng(23.027133233707815, 72.56050555260956))
                    .zoom(10f).build()
            )
        )
    }


    override fun onMyLocationButtonClick(): Boolean {
        return false
    }

    @SuppressLint("MissingPermission")
    private fun mapReset() {
        fusedLocationProviderClient.lastLocation.addOnCompleteListener {
            val lastKnownLocation = LatLng(
                it.result.latitude, it.result.longitude
            )
            for (polyline in polylineList) {
                polyline.remove()
            }
            map.animateCamera(
                CameraUpdateFactory.newCameraPosition(
                    setCameraPosition(lastKnownLocation)
                )
            )
            locationList.clear()
            for (marker in markerList) {
                marker.remove()
            }
            markerList.clear()
        }
    }

    private fun observeTrackerService() {
        Tracker.locationLiveData.observe(viewLifecycleOwner) {
            if (it != null) {
                locationList = it
                if (locationList.size > 1) {
//                   TODO send the last location post request
                }
                drawPolyline()
                followPolyline()
            }
        }
        Tracker.started.observe(viewLifecycleOwner) {
            started = it
        }

        Tracker.startTime.observe(viewLifecycleOwner) {
            starTime = it
        }
        Tracker.stopTime.observe(viewLifecycleOwner) {
            stopTime = it
            if (stopTime != 0L) {
                showBiggerPicture()
//               TODO show the time and distance to user
            }
        }
    }


    private fun showBiggerPicture() {
        if (locationList.size > 5) {
            Log.d("tracking", "showBiggerPicture: if started")
            val bounds = LatLngBounds.Builder()
            for (location in locationList) {
                bounds.include(location)
            }
            map.animateCamera(
                CameraUpdateFactory.newLatLngBounds(bounds.build(), 500),
                2000,
                null
            )
            addMarker(locationList.first())
            addMarker(locationList.last())
        } else {
            Log.d("tracking", "showBiggerPicture: else started")
            Toast.makeText(
                requireContext(),
                "The movement of device is very less",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun addMarker(position: LatLng) {
        val marker = map.addMarker(MarkerOptions().position(position))
        marker?.let { markerList.add(it) }
    }

    private fun showResult() {
//        val result = Result(
//            distanceTraveled(locationList),
//            calculateElapsedTime(starTime, stopTime)
//        )
//        lifecycleScope.launch {
//            delay(2500)
//            val directions = MapsFragmentDirections.actionMapsFragmentToResultFragment(result)
//            findNavController().navigate(directions)
//
//        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        onStartCommands()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(requireActivity()).build().show()
        } else {
            if (requestCode == PERMISSION_LOCATION_REQUEST_CODE) {
                requestLocationPermission(this)
            } else if (requestCode == BACKGROUND_PERMISSION_REQUEST_CODE) {
                requestBackgroundPermission(this)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onMarkerClick(p0: Marker): Boolean {
        return true
    }


    private fun followPolyline() {
        if (locationList.isNotEmpty()) {
            map.animateCamera(
                (CameraUpdateFactory.newCameraPosition(
                    setCameraPosition(
                        locationList.last()
                    )
                )), 1000, null
            )
        }
    }

    private fun drawPolyline() {
        val polyline = map.addPolyline(
            PolylineOptions().apply {
                width(10f)
                color(Color.BLUE)
                startCap(ButtCap())
                endCap(ButtCap())
                jointType(JointType.ROUND)
                addAll(locationList)
            }
        )
        polylineList.add(polyline)
    }
}