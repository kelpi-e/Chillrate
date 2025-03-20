package com.neurotech.callibrineurosdkdemo.screens.spectrum

import android.telecom.Call
import android.util.Log
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableDouble
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.neurotech.callibrineurosdkdemo.callibri.CallibriController
import com.neurotech.callibrineurosdkdemo.callibri.SpectrumController
import com.neurotech.spectrum.RawSpectrumData
import com.neurotech.spectrum.WavesSpectrumData
import kotlin.math.roundToInt

class SpectrumViewModel : ViewModel() {
    var started = ObservableBoolean(false)

    val spectrumBins: MutableLiveData<List<Double>> by lazy {
        MutableLiveData<List<Double>>()
    }

    var alpha = ObservableInt (0)
    var beta = ObservableInt(0)
    var delta = ObservableInt(0)
    var theta = ObservableInt(0)
    var gamma = ObservableInt(0)

    private val spectrumController = SpectrumController(CallibriController.samplingFrequency!!.toInt())
    val sf = (CallibriController.samplingFrequency?.div(spectrumController.fftBinsFor1Hz))?.toFloat()

    fun onStartClicked(){
        if(started.get()){
            CallibriController.stopSignal()
        }
        else{
            spectrumController.processedSpectrum = {
                val res = arrayListOf<Double>()
                for (sample in it) {
                    res.addAll(sample.allBinsValues.toList())
                }

                spectrumBins.postValue(res)
            }
            spectrumController.processedWaves = {
                for (sample in it) {
                    alpha.set((sample.alpha_rel * 100).roundToInt())
                    beta.set((sample.beta_rel * 100).roundToInt())
                    delta.set((sample.delta_rel * 100).roundToInt())
                    theta.set((sample.theta_rel * 100).roundToInt())
                    gamma.set((sample.gamma_rel * 100).roundToInt())
                }
            }

            CallibriController.startSignal(signalReceived = {
                val res = arrayListOf<Double>()
                for (sample in it) {
                    res.addAll(sample.samples.toList())
                }
                spectrumController.processSamples(res.toDoubleArray())
            })
        }
        started.set(!started.get())
    }

    fun close(){
        CallibriController.stopSignal()
        spectrumController.processedSpectrum = { }
        spectrumController.processedWaves = { }
    }
}