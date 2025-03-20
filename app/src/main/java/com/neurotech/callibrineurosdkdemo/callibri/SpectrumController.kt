package com.neurotech.callibrineurosdkdemo.callibri

import com.neurotech.spectrum.RawSpectrumData
import com.neurotech.spectrum.SpectrumMath
import com.neurotech.spectrum.WavesSpectrumData
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking

class SpectrumController(samplingRate: Int) {

    private val processWinRate = 20 // Hz
    private val fft_window = 4000
    private var bord_frequency = 50
    var delta_coef = 1.0
    var theta_coef = 1.0
    var alpha_coef = 1.0
    var beta_coef = 1.0
    var gamma_coef = 1.0

    private val spectrumMath: SpectrumMath

    var processedSpectrum: (Array<RawSpectrumData>) -> Unit = { }
    var processedWaves: (Array<WavesSpectrumData>) -> Unit = { }

    init {
        spectrumMath = SpectrumMath(samplingRate, fft_window, processWinRate)
        spectrumMath.initParams(bord_frequency, true)
        spectrumMath.setWavesCoeffs(delta_coef, theta_coef, alpha_coef, beta_coef, gamma_coef)
    }

    val fftBinsFor1Hz get() = spectrumMath.fftBinsFor1Hz

    fun processSamples(samples: DoubleArray){
        try {
            spectrumMath.pushAndProcessData(samples)

            val rawSpectrumData: Array<RawSpectrumData> = spectrumMath.readRawSpectrumInfoArr()
            val wavesSpectrumData: Array<WavesSpectrumData> = spectrumMath.readWavesSpectrumInfoArr()

            spectrumMath.setNewSampleSize()

            if(rawSpectrumData.isNotEmpty()){
                processedSpectrum(rawSpectrumData)
            }
            processedWaves(wavesSpectrumData)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun finish(){
        spectrumMath.clearData()
    }

}