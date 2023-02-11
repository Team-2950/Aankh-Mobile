package com.example.aankh

import android.os.Bundle
import android.view.Gravity
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.example.aankh.databinding.ActivityDashBoardBinding
import com.example.aankh.service.Tracker
import com.example.aankh.utils.Constants.NOTIFICATION_CHANNEL_NAME
import com.example.aankh.utils.Permissions.hasLocationPermission
import com.example.aankh.utils.Permissions.requestLocationPermission
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

@AndroidEntryPoint
class DashBoard : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration

    private lateinit var binding: ActivityDashBoardBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val drawerButton = findViewById<ImageView>(R.id.drawer_menu_button)
        val navController = findNavController(R.id.nav_host_fragment_content_main)


        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.mainDashboard,
                R.id.trackingFragment,
                R.id.firFragment,
                R.id.profileFragment,
                R.id.logOutFragment,
                R.id.notificationFragment
            ), binding.drawerLayout
        )

        val comeFrom = intent.getStringExtra("comeFrom")
        if (comeFrom.equals(NOTIFICATION_CHANNEL_NAME))
            navController.navigate(R.id.action_mainDashboard_to_trackingFragment)



        binding.navView.setupWithNavController(navController)



        drawerButton.setOnClickListener {
            binding.drawerLayout.openDrawer(Gravity.LEFT)
        }


    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(Gravity.LEFT))
            binding.drawerLayout.close()
        else
            super.onBackPressed()
    }
}