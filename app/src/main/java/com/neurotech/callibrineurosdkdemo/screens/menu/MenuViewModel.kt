package com.neurotech.callibrineurosdkdemo.screens.menu

import android.widget.Toast
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import com.neurosdk2.neuro.types.SensorState
import com.neurotech.callibrineurosdkdemo.R
import com.neurotech.callibrineurosdkdemo.callibri.CallibriController

class MenuViewModel : ViewModel() {
    var connected = ObservableBoolean(false)
    var hasDevice = ObservableBoolean(CallibriController.hasDevice)
    var hasEnvelope = ObservableBoolean(CallibriController.isEnvelope!!)
    var hasSignal = ObservableBoolean(CallibriController.isSignal!!)

    fun reconnect(){
        if(CallibriController.connectionState == SensorState.StateInRange){
            CallibriController.disconnectCurrent()
            connected.set(false)
        }else {
            CallibriController.connectCurrent(onConnectionResult = {
                connected.set(it == SensorState.StateInRange)
            })
        }
    }

    fun updateDeviceInfo() {
        connected.set(CallibriController.connectionState == SensorState.StateInRange)
        hasDevice.set(CallibriController.hasDevice)
    }
}