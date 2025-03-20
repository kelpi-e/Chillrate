package com.neurotech.callibrineurosdkdemo.screens.search

import android.util.Log
import androidx.databinding.*
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.neurosdk2.neuro.types.SensorInfo
import com.neurotech.callibrineurosdkdemo.callibri.CallibriController

class SearchScreenViewModel : ViewModel(){

    var started = ObservableBoolean(false)

    private val _sensors = MutableLiveData<List<SensorInfo>>()
    val sensors: LiveData<List<SensorInfo>> get() = _sensors

    fun onSearchClicked(){
        if(started.get()){
            CallibriController.stopSearch()
        }
        else{
            CallibriController.disconnectCurrent()
            CallibriController.closeSensor()

            CallibriController.startSearch(sensorsChanged = { scanner, infos ->
                run {
                    _sensors.postValue(infos)
                }
            })
        }
        started.set(!started.get())
    }


    fun close(){
        CallibriController.stopSearch()
    }
}

