package com.neurotech.callibrineurosdkdemo.screens.emotions

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import com.neurotech.callibrineurosdkdemo.callibri.CallibriController
import com.neurotech.callibrineurosdkdemo.emotions.EmotionalMath
import com.neurotech.callibrineurosdkdemo.emotions.EmotionalMathConfig
import com.neurotech.emstartifcats.ArtifactDetectSetting
import com.neurotech.emstartifcats.MathLibSetting
import com.neurotech.emstartifcats.MentalAndSpectralSetting
import com.neurotech.emstartifcats.ShortArtifactDetectSetting
import com.neurotech.callibrineurosdkdemo.storage.HistoryStorage

class EmotionsViewModel : ViewModel() {

    //<editor-fold desc="Properties">
    private var emotionalMath: EmotionalMath
    var started = ObservableBoolean(false)
    private var calibrated = ObservableBoolean(false)
    var artifacted = ObservableBoolean(false)
    var calibrationProgress = ObservableField(0)
    var relaxationText = ObservableField("Relaxation: Waiting for calibration...")
    var attentionText = ObservableField("Attention: Waiting for calibration...")
    var relRelaxationText = ObservableField("Rel Relaxation: Waiting for calibration...")
    var relAttentionText = ObservableField("Rel Attention: Waiting for calibration...")
    var alphaText = ObservableField("Alpha: Waiting for calibration...")
    var betaText = ObservableField("Beta: Waiting for calibration...")
    var gammaText = ObservableField("Gamma: Waiting for calibration...")
    var thetaText = ObservableField("Theta: Waiting for calibration...")
    var deltaText = ObservableField("Delta: Waiting for calibration...")
    //</editor-fold>

    // последние спектральные значения, обновляются в lastSpectralData
    private var lastAlpha = 0.0
    private var lastBeta = 0.0
    private var lastGamma = 0.0
    private var lastTheta = 0.0
    private var lastDelta = 0.0

    //<editor-fold desc="Init and Close">
    init {
        val samplingFrequencyHz = CallibriController.samplingFrequency?.toInt()
        emotionalMath = samplingFrequencyHz?.let { getEmotionalConfig(it) }?.let { EmotionalMath(it) }!!
    }

    fun close() {
        CallibriController.stopSignal()
    }
    //</editor-fold>

    //<editor-fold desc="Buttons">
    fun onSignalClicked() {
        if (started.get()) {
            CallibriController.stopSignal()
        } else if (!calibrated.get()) {
            startSignal()
            emotionalMath.startCalibration()
        } else {
            startSignal()
        }
        started.set(!started.get())
    }
    //</editor-fold>

    //<editor-fold desc="Signal">
    private fun startSignal() {
        emotionalMath.isCalibrationSuccess = { calibrated.set(it) }
        emotionalMath.isArtifacted = { artifacted.set(it) }

        emotionalMath.lastMindData = {
            relaxationText.set("Relaxation: " + String.format("%.2f", it.instRelaxation) + "%")
            attentionText.set("Attention: " + String.format("%.2f", it.instAttention) + "%")
            relRelaxationText.set("Rel Relaxation: " + String.format("%.2f", it.relRelaxation))
            relAttentionText.set("Rel Attention: " + String.format("%.2f", it.relAttention))

            // Сохраняем запись в файл (используем последние спектральные значения)
            try {
                val entry = com.neurotech.callibrineurosdkdemo.storage.HistoryStorage.LoggedEntry(
                    ts = System.currentTimeMillis(),
                    instRelaxation = it.instRelaxation.toDouble()/*,
                    instAttention = it.instAttention.toDouble(),
                    relRelaxation = it.relRelaxation.toDouble(),
                    relAttention = it.relAttention.toDouble(),
                    alpha = lastAlpha,
                    beta = lastBeta,
                    gamma = lastGamma,
                    theta = lastTheta,
                    delta = lastDelta*/
                )
                HistoryStorage.saveEntry(entry)
            } catch (_: Exception) { /* ignore saving errors */ }
        }

        emotionalMath.calibrationProgress = { calibrationProgress.set(it) }

        emotionalMath.lastSpectralData = {
            alphaText.set("Alpha: " + String.format("%.2f", it.alpha) + "%")
            betaText.set("Beta: " + String.format("%.2f", it.beta) + "%")
            gammaText.set("Gamma: " + String.format("%.2f", it.gamma) + "%")
            thetaText.set("Theta: " + String.format("%.2f", it.theta) + "%")
            deltaText.set("Delta: " + String.format("%.2f", it.delta) + "%")

            // обновляем локальные переменные, чтобы при следующей записи mind-данных спектр был доступен
            lastAlpha = it.alpha
            lastBeta = it.beta
            lastGamma = it.gamma
            lastTheta = it.theta
            lastDelta = it.delta
        }

        CallibriController.startSignal { samplesList ->
            val res = mutableListOf<Double>()
            for (sample in samplesList) {
                res.addAll(sample.samples.toList())
            }
            emotionalMath.pushData(res.toDoubleArray())
        }
    }
    //</editor-fold>

    //<editor-fold desc="Emotional Config">
    private fun getEmotionalConfig(samplingFrequencyHz: Int): EmotionalMathConfig {
        val lastWinsToAvg = 3
        val poorSignalAvgSize = 5
        val poorSignalAvgTrigger = .8F
        val mathLibSetting = MathLibSetting(
            samplingFrequencyHz,
            25,
            samplingFrequencyHz * 2,
            6,
            false,
            1,
            0
        )
        val artifactDetectSetting = ArtifactDetectSetting(
            110, 70, 800_000, (3*1e7).toInt(), 4, false, false, true, 100
        )
        val shortArtifactDetectSetting = ShortArtifactDetectSetting(200, 200, 25)
        val mentalAndSpectralSetting = MentalAndSpectralSetting(2, 2)
        return EmotionalMathConfig(
            samplingFrequencyHz,
            lastWinsToAvg,
            poorSignalAvgSize,
            poorSignalAvgTrigger,
            mathLibSetting,
            artifactDetectSetting,
            shortArtifactDetectSetting,
            mentalAndSpectralSetting
        )
    }
    //</editor-fold>
}
