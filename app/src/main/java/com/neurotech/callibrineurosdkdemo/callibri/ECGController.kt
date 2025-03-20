package com.neurotech.callibrineurosdkdemo.callibri

import com.neurotech.callibriutils.CallibriMath
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking

data class ECGData(val rr: Double, val hr: Double, val pi: Double,
                   val moda: Double, val AmpModa: Double, val variationDist: Double)

class ECGController(samplingRate: Int) {

    private val dataWindow = (samplingRate / 2)
    private val nwinsForPressureIndex = 30

    private var ecgMath: CallibriMath

    var processedData: (ECGData) -> Unit = { }

    init {

        ecgMath = CallibriMath(samplingRate, dataWindow, nwinsForPressureIndex)
    }

    fun processSamples(samples: DoubleArray) {
        try {
            ecgMath.pushData(samples)

            if(ecgMath.rrDetected()){
                val rr = ecgMath.rr
                val hr = ecgMath.hr
                val pi = ecgMath.pressureIndex
                val moda = ecgMath.moda
                val amplModa = ecgMath.amplModa
                val variationDist = ecgMath.variationDist

                processedData(ECGData(rr, hr, pi, moda, amplModa, variationDist))

                ecgMath.setRRchecked()
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun finish(){
        ecgMath.clearData()
    }
}