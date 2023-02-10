package com.example.aankh.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.example.aankh.utils.Constants.ACTION_SERVICE_START
import com.example.aankh.utils.Constants.ACTION_SERVICE_STOP
import com.example.aankh.utils.Constants.LOCATION_FASTEST_UPDATE_INTERVAL
import com.example.aankh.utils.Constants.LOCATION_UPDATE_INTERVAL
import com.example.aankh.utils.Constants.NOTIFICATION_CHANNEL_ID
import com.example.aankh.utils.Constants.NOTIFICATION_CHANNEL_NAME
import com.example.aankh.utils.Constants.NOTIFICATION_ID
import com.example.aankh.utils.MapUtil
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class Tracker : LifecycleService() {
    @Inject
    lateinit var notificationManager: NotificationManager

    @Inject
    lateinit var notification: NotificationCompat.Builder
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient


    companion object {
        var started = MutableLiveData<Boolean>()
        val locationLiveData = MutableLiveData<MutableList<LatLng>>()
        val startTime = MutableLiveData<Long>()
        val stopTime = MutableLiveData<Long>()
    }


    private fun setInitialValues() {
        started.postValue(false)
        locationLiveData.postValue(mutableListOf())
        startTime.postValue(0)
        stopTime.postValue(0)
    }


    private fun updateLocationList(location: Location) {
        val newLatLng = LatLng(location.latitude, location.longitude)
        locationLiveData.value?.apply {
            add(newLatLng)
            locationLiveData.postValue(this)
        }
    }


    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
            result?.locations?.let { locations ->
                for (location in locations) {
                    updateLocationList(location)
                    updateNotificationPeriodically()
                }
            }
        }
    }


    //    the system invokes this method to perform one-time setup
//        before onStartCommand() or onBind(), basically used to create the service
    override fun onCreate() {
        setInitialValues()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        super.onCreate()
    }


    //    when we call startService() method then this function method is triggerd
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_SERVICE_START -> {
                    startForegroundService()
                    startLocationUpdates()
                    started.postValue(true)
                }
                ACTION_SERVICE_STOP -> {
                    started.postValue(false)
                    stopForegoundService()
                }
            }


        }

        return super.onStartCommand(intent, flags, startId)
    }


    private fun startForegroundService() {
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, notification.build())
    }


    private fun stopForegoundService() {

            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).cancel(
                NOTIFICATION_ID
            )
            stopForeground(true)
            stopSelf()
            stopTime.postValue(System.currentTimeMillis())


    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        val locationRequest = LocationRequest().apply {
            interval = LOCATION_UPDATE_INTERVAL
            fastestInterval = LOCATION_FASTEST_UPDATE_INTERVAL
            Priority.PRIORITY_HIGH_ACCURACY
        }
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest, locationCallback, Looper.getMainLooper()
        )
        startTime.postValue(System.currentTimeMillis())
    }


    private fun updateNotificationPeriodically() {
        notification.apply {
            setContentTitle("Distance Travelled")
            setContentText(locationLiveData.value?.let {
                MapUtil.distanceTraveled(it)
            } + "km")
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }

    }
}
