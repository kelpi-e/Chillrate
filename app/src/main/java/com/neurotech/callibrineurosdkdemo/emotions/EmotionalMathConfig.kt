package com.neurotech.callibrineurosdkdemo.emotions

import com.neurotech.emstartifcats.ArtifactDetectSetting
import com.neurotech.emstartifcats.MathLibSetting
import com.neurotech.emstartifcats.MentalAndSpectralSetting
import com.neurotech.emstartifcats.ShortArtifactDetectSetting

class EmotionalMathConfig(
    val samplingFrequencyHz: Int,
    val lastWinsToAvg: Int,
    val poorSignalAvgSize: Int,
    val poorSignalAvgTrigger: Float,
    val mathLibSetting: MathLibSetting,
    val artifactDetectSetting: ArtifactDetectSetting,
    val shortArtifactDetectSetting: ShortArtifactDetectSetting,
    val mentalAndSpectralSetting: MentalAndSpectralSetting
)