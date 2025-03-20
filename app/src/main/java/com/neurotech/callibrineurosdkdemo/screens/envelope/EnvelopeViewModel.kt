package com.neurotech.callibrineurosdkdemo.screens.envelope

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.neurotech.callibrineurosdkdemo.callibri.CallibriController

class EnvelopeViewModel : ViewModel() {
    var started = ObservableBoolean(false)

    val samples: MutableLiveData<List<Double>> by lazy {
        MutableLiveData<List<Double>>()
    }

    fun onStartClicked(){
        if(started.get()){
            CallibriController.stopEnvelope()
        }
        else{
            CallibriController.startEnvelope {
                val res = Array(it.size) { i -> it[i].sample }.toList()
                samples.postValue(res)
            }
        }
        started.set(!started.get())
    }

    fun close(){
        CallibriController.stopEnvelope()
    }
}