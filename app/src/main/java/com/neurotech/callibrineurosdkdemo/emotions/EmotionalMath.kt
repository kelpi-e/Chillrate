package com.neurotech.callibrineurosdkdemo.emotions

import android.util.Log
import com.neurotech.emstartifcats.EmotionalMath
import com.neurotech.emstartifcats.MindData
import com.neurotech.emstartifcats.RawChannelsArray
import com.neurotech.emstartifcats.SpectralDataPercents
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking

class EmotionalMath(config: EmotionalMathConfig) {
    //<editor-fold desc="Properties">
    private val lastWinsForAvg = config.lastWinsToAvg
    private val useAvgMind = config.lastWinsToAvg > 0

    private var lastCalibrationPercent = -1

    private var isCalibration = false

    private val brainMath = EmotionalMath(
        config.mathLibSetting,
        config.artifactDetectSetting,
        config.shortArtifactDetectSetting,
        config.mentalAndSpectralSetting
    )

    var lastMindData: ((MindData) -> Unit) = {}
    var lastSpectralData: (SpectralDataPercents) -> Unit = {}
    var isCalibrationSuccess: ((Boolean) -> Unit) = {}
    var calibrationProgress: ((Int) -> Unit) = {}
    var isArtifacted: ((Boolean) -> Unit) = {}
    //</editor-fold>

    //<editor-fold desc="Data">
    fun pushData(data: DoubleArray) {
            try {
                brainMath.pushDataArr(Array(1) {
                    RawChannelsArray(data)
                })

                brainMath.processDataArr()

                isArtifacted()

                if (isCalibration) {
                    processCalibration()
                } else {
                    calcData()

                    getSpectralData()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    private fun calcData() {
        if (useAvgMind) {
            lastMindData(brainMath.readAverageMentalData(lastWinsForAvg))
        } else {
            val mindData = brainMath.readMentalDataArr()

            if (mindData != null && mindData.isNotEmpty()) {
                lastMindData(getMindAvg(mindData))
            }
        }
    }

    private fun getSpectralData() {
        val spectralData = brainMath.readSpectralDataPercentsArr()

        if (spectralData.isNotEmpty()) {
            lastSpectralData(spectralData.last())
        }
    }
    //</editor-fold>

    //<editor-fold desc="Mind Values">
    private fun getMindAvg(data: Array<MindData>): MindData {
        if (data.isEmpty()) {
            return MindData(0.0, 0.0, 0.0, 0.0)
        }

        if (data.size == 1) {
            return data[0]
        }

        val res = MindData(0.0, 0.0, 0.0, 0.0)

        for (mindData in data) {
            res.relAttention += mindData.relAttention
            res.relRelaxation += mindData.relRelaxation
            res.instAttention += mindData.instAttention
            res.instRelaxation += mindData.instRelaxation
        }

        res.relAttention /= data.size
        res.relRelaxation /= data.size
        res.instAttention /= data.size
        res.instRelaxation /= data.size
        brainMath.readSpectralDataPercentsArr()

        return res
    }
    //</editor-fold>

    //<editor-fold desc="Artifacts">
    private fun isArtifacted() {
        if (isCalibration) {
            isArtifacted(brainMath.isBothSidesArtifacted)
        }
        isArtifacted(brainMath.isArtifactedSequence)
    }
    //</editor-fold>

    //<editor-fold desc="Calibration">
    fun startCalibration() {
        Log.d(::EmotionalMath.name, "Calibration started")

        brainMath.startCalibration()

        isCalibration = true
    }

    private fun isCalibrationSuccess(): Boolean {
        return brainMath.calibrationFinished()
    }

    private fun processCalibration() {
        if (isCalibrationSuccess()) {
            onCalibrationEnd()
        } else {
            val progress = brainMath.callibrationPercents

            if (progress != lastCalibrationPercent) {
                calibrationProgress(progress)

                lastCalibrationPercent = progress
            }
//            Log.d(::EmotionalMath.name, "Calibration percents: $progress")
        }
    }

    private fun onCalibrationEnd() {
        calibrationProgress(100)

        isCalibration = false

        Log.d(::EmotionalMath.name, "Calibration ended")
    }
    //</editor-fold>
}