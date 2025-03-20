package com.neurotech.callibrineurosdkdemo

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.color.DynamicColors
import com.google.android.material.elevation.SurfaceColors
import com.neurosdk2.neuro.types.SensorState
import com.neurotech.callibrineurosdkdemo.callibri.CallibriController
import com.neurotech.callibrineurosdkdemo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        DynamicColors.applyToActivityIfAvailable(this)

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val color = SurfaceColors.SURFACE_2.getColor(this)
        window.statusBarColor = color

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.navigationBarColor = getColor(R.color.colorDevState)
        }

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        CallibriController.connectionStateChanged = {
            runOnUiThread {
                binding.txtDevState.text =
                    if (it == SensorState.StateInRange) getString(R.string.dev_state_connected)
                    else getString(R.string.dev_state_disconnected)

                if (it == SensorState.StateOutOfRange) {
                    binding.txtDevBatteryPower.text = getString(R.string.dev_power_prc, 0)
                    navController.popBackStack(R.id.MenuFragment, false)
                }
            }
        }

        CallibriController.batteryChanged = {
            runOnUiThread {
                binding.txtDevBatteryPower.text = getString(R.string.dev_power_prc, it)
            }

        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}