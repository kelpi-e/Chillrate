package com.neurotech.callibrineurosdkdemo.screens.ecg

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableDouble
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.neurotech.callibrineurosdkdemo.callibri.CallibriController
import com.neurotech.callibrineurosdkdemo.callibri.ECGController
import com.neurotech.callibrineurosdkdemo.callibri.ECGData
import kotlin.math.roundToInt

class ECGViewModel : ViewModel() {

    var started = ObservableBoolean(false)

    val samples: MutableLiveData<List<Double>> by lazy {
        MutableLiveData<List<Double>>()
    }

    val ecgController = ECGController(CallibriController.samplingFrequency!!.toInt())

    var ecgData = ObservableField(ECGData(0.0,0.0,0.0,0.0,0.0,0.0))

    fun onStartClicked(){
        if(started.get()){
            CallibriController.stopSignal()
        }
        else{
            ecgController.processedData = {
                ecgData.set(it)
            }
            CallibriController.startSignal(signalReceived = {
                val res = arrayListOf<Double>()
                for (sample in it) {
                    res.addAll(sample.samples.toList())
                }
                samples.postValue(res)
                ecgController.processSamples(res.toDoubleArray())
            })
        }
        started.set(!started.get())
    }

    fun close(){
        CallibriController.stopSignal()
    }
}