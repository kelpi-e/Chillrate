package com.neurotech.callibrineurosdkdemo.callibri

import com.neurosdk2.neuro.Callibri
import com.neurosdk2.neuro.Scanner
import com.neurosdk2.neuro.Sensor
import com.neurosdk2.neuro.interfaces.CallibriElectrodeStateChanged
import com.neurosdk2.neuro.interfaces.CallibriEnvelopeDataReceived
import com.neurosdk2.neuro.interfaces.CallibriSignalDataReceived
import com.neurosdk2.neuro.types.*
import kotlinx.coroutines.runBlocking
import kotlin.concurrent.thread
import kotlinx.coroutines.*

object CallibriController {

    //<editor-fold desc="Scanner">
    var scanner: Scanner? = null

    fun startSearch(sensorsChanged: (Scanner, List<SensorInfo>) -> Unit) {
        try {
            if (scanner == null) {
                scanner = Scanner(SensorFamily.SensorLECallibri, SensorFamily.SensorLEKolibri)
            } else {
                sensorsChanged(scanner!!, scanner!!.sensors)
            }
            scanner?.sensorsChanged = Scanner.ScannerCallback(sensorsChanged)
            scanner?.start()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun stopSearch() {
        try {
            scanner?.stop()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
    //</editor-fold>

    //<editor-fold desc="Sensor state">
    private var sensor: Callibri? = null

    var connectionStateChanged: (SensorState) -> Unit = { }
    var batteryChanged: (Int) -> Unit = { }

    fun createAndConnect(sensorInfo: SensorInfo, onConnectionResult: (SensorState) -> Unit) {
        thread {
            try {
                sensor = scanner?.createSensor(sensorInfo) as Callibri

                if (sensor != null) {
                    sensor?.sensorStateChanged = Sensor.SensorStateChanged(connectionStateChanged)
                    sensor?.batteryChanged = Sensor.BatteryChanged(batteryChanged)

                    try {
                        sensor?.samplingFrequency = SensorSamplingFrequency.FrequencyHz1000
                        sensor?.signalType = CallibriSignalType.ECG
                        sensor?.hardwareFilters = listOf(SensorFilter.FilterBSFBwhLvl2CutoffFreq45_55Hz,
                            SensorFilter.FilterHPFBwhLvl1CutoffFreq1Hz);
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }

                    connectionStateChanged(SensorState.StateInRange)
                    onConnectionResult(SensorState.StateInRange)
                } else {
                    onConnectionResult(SensorState.StateOutOfRange)
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }

        }
    }

    fun connectCurrent(onConnectionResult: (SensorState) -> Unit) {
        if (sensor?.state == SensorState.StateOutOfRange) {
            thread {
                try {
                    sensor?.connect()
                    onConnectionResult(sensor!!.state)
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
        }
    }

    fun disconnectCurrent() {
        try {
            if (sensor?.state == SensorState.StateInRange)
                sensor?.disconnect()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

    }

    fun closeSensor() {
        try {
            sensor?.close()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    val connectionState get() = sensor?.state

    val hasDevice: Boolean
        get() {
            return sensor != null
        }
    //</editor-fold>

    //<editor-fold desc="Parameters">
    val isSignal: Boolean
        get() {
            return if (connectionState == SensorState.StateInRange) sensor!!.isSupportedCommand(
                SensorCommand.StartSignal
            ) else false
        }

    val isEnvelope: Boolean
        get() {
            return if (connectionState == SensorState.StateInRange) sensor!!.isSupportedCommand(
                SensorCommand.StartEnvelope
            ) else false
        }

    val samplingFrequency get() = sensor?.samplingFrequency?.toFloat()

    fun fullInfo(): String {
        val info = buildString {
            append("Parameters: ")
            for (param in sensor!!.supportedParameter) {
                append("\n\tName: ${param.param.name}")
                append("\n\t\tAccess: ${param.paramAccess}")
                when (param.param!!) {
                    SensorParameter.ParameterGain -> append("\n\t\tValue: ${sensor?.gain}")
                    SensorParameter.ParameterHardwareFilterState -> {

                    }
                    SensorParameter.ParameterFirmwareMode -> append("\n\t\tValue: ${sensor?.firmwareMode}")
                    SensorParameter.ParameterSamplingFrequency -> append("\n\t\tValue: ${sensor?.samplingFrequency}")
                    SensorParameter.ParameterOffset -> append("\n\t\tValue: ${sensor?.dataOffset}")
                    SensorParameter.ParameterExternalSwitchState -> append("\n\t\tValue: ${sensor?.extSwInput}")
                    SensorParameter.ParameterADCInputState -> append("\n\t\tValue: ${sensor?.adcInput}")
                    SensorParameter.ParameterAccelerometerSens -> append("\n\t\tValue: ${sensor?.accSens}")
                    SensorParameter.ParameterGyroscopeSens -> append("\n\t\tValue: ${sensor?.gyroSens}")
                    SensorParameter.ParameterStimulatorAndMAState -> {
                        append("\n\t\tStimulator state: ${sensor?.stimulatorMAState?.stimulatorState}")
                        append("\n\t\tMA state: ${sensor?.stimulatorMAState?.maState}")
                    }
                    SensorParameter.ParameterStimulatorParamPack -> {
                        append("\n\t\tCurrent: ${sensor?.stimulatorParam?.current}")
                        append("\n\t\tFrequency: ${sensor?.stimulatorParam?.frequency}")
                        append("\n\t\tPulse width: ${sensor?.stimulatorParam?.pulseWidth}")
                        append("\n\t\tStimulus duration: ${sensor?.stimulatorParam?.stimulusDuration}")
                    }
                    SensorParameter.ParameterMotionAssistantParamPack -> {
                        append("\n\t\tGyro start: ${sensor?.motionAssistantParam?.gyroStart}")
                        append("\n\t\tGyro stop: ${sensor?.motionAssistantParam?.gyroStop}")
                        append("\n\t\tLimb: ${sensor?.motionAssistantParam?.limb}")
                        append("\n\t\tMin pause (in ms): ${sensor?.motionAssistantParam?.minPauseMs}")
                    }
                    SensorParameter.ParameterFirmwareVersion -> {
                        append("\n\t\tFW version: ${sensor?.version?.fwMajor}.${sensor?.version?.fwMinor}.${sensor?.version?.fwPatch}")
                        append("\n\t\tHW version: ${sensor?.version?.hwMajor}.${sensor?.version?.hwMinor}.${sensor?.version?.hwPatch}")
                        append("\n\t\tExtension: ${sensor?.version?.extMajor}")
                    }
                    SensorParameter.ParameterMEMSCalibrationStatus -> append("\n  Value: ${sensor?.memsCalibrateState}")
                    SensorParameter.ParameterMotionCounterParamPack -> {
                        append("\n\t\tInsense threshold MG: ${sensor?.motionCounterParam?.insenseThresholdMG}")
                        append("\n\t\tInsense threshold sample: ${sensor?.motionCounterParam?.insenseThresholdSample}")
                    }
                    SensorParameter.ParameterMotionCounter -> {
                        append("\n\t\tInsense threshold MG: ${sensor?.motionCounter}")
                        append("\n\t\tInsense threshold sample: ${sensor?.motionCounterParam?.insenseThresholdSample}")
                    }
                    SensorParameter.ParameterBattPower -> append("\n\t\tValue: ${sensor?.battPower}")
                    SensorParameter.ParameterSensorFamily -> append("\n\t\tValue: ${sensor?.sensFamily}")
                    SensorParameter.ParameterSensorMode -> append("\n\t\tValue: ${sensor?.firmwareMode}")
                    SensorParameter.ParameterSensorChannels -> append("\n\t\tValue: ${sensor?.channelsCount}")
                    SensorParameter.ParameterSamplingFrequencyResp -> append("\n\t\tValue: ${sensor?.samplingFrequencyResp}")
                    SensorParameter.ParameterName -> append("\n\t\tValue: ${sensor?.name}")
                    SensorParameter.ParameterState -> append("\n\t\tValue: ${sensor?.state}")
                    SensorParameter.ParameterAddress -> append("\n\t\tValue: ${sensor?.address}")
                    SensorParameter.ParameterSerialNumber -> append("\n\t\tValue: ${sensor?.serialNumber}")
                    else -> break;
                }
            }

            append("\tColor: ${sensor?.color}")

            if (sensor?.isSupportedFeature(SensorFeature.Signal) == true) {
                append("\nSignal type: \n\t${sensor?.signalType}")
            }

            append("\n\nCommands: ")
            for (command in sensor?.supportedCommand!!) {
                append("\n\t${command.name}")
            }

            append("\n\nFeatures: ")
            for (feature in sensor?.supportedFeature!!) {
                append("\n\t${feature.name}")
            }
        }

        return info
    }

    var signalType
        get() = sensor?.signalType
        set(value) {
            sensor?.signalType = value
        }
    //</editor-fold>

    //<editor-fold desc="Signal">
    fun startSignal(signalReceived: (Array<CallibriSignalData>) -> Unit) {
        sensor?.callibriSignalDataReceived = CallibriSignalDataReceived(signalReceived)
        executeCommand(SensorCommand.StartSignal)
    }

    fun stopSignal() {
        sensor?.callibriSignalDataReceived = null
        executeCommand(SensorCommand.StopSignal)
    }
    //</editor-fold>

    //<editor-fold desc="Electrodes">
    fun startElectrodes(electrodesStateChanged: (CallibriElectrodeState) -> Unit) {
        sensor?.callibriElectrodeStateChanged =
            CallibriElectrodeStateChanged(electrodesStateChanged)
        executeCommand(SensorCommand.StartSignal)
    }

    fun stopElectrodes() {
        sensor?.callibriElectrodeStateChanged = null
        executeCommand(SensorCommand.StopSignal)
    }
    //</editor-fold>

    //<editor-fold desc="Envelope">
    fun startEnvelope(envelopeReceived: (Array<CallibriEnvelopeData>) -> Unit) {
        sensor?.callibriEnvelopeDataReceived = CallibriEnvelopeDataReceived(envelopeReceived)
        executeCommand(SensorCommand.StartEnvelope)
    }

    fun stopEnvelope() {
        sensor?.callibriEnvelopeDataReceived = null

        executeCommand(SensorCommand.StopEnvelope)
    }
    //</editor-fold>

    @OptIn(DelicateCoroutinesApi::class)
    private fun executeCommand(command: SensorCommand) =
        runBlocking(newSingleThreadContext("dedicatedThread")) {
            try {
                if (sensor?.isSupportedCommand(command) == true) {
                    sensor?.execCommand(command)
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
}

fun SensorSamplingFrequency.toFloat(): Float {
    return when (this) {
        SensorSamplingFrequency.FrequencyHz125 -> 125F
        SensorSamplingFrequency.FrequencyHz250 -> 250F
        SensorSamplingFrequency.FrequencyHz500 -> 500F
        SensorSamplingFrequency.FrequencyHz1000 -> 1000F
        SensorSamplingFrequency.FrequencyHz2000 -> 2000F
        SensorSamplingFrequency.FrequencyUnsupported -> 0.0f
        else -> 0.0f
    }
}