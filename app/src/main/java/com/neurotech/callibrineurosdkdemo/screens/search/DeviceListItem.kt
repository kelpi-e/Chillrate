package com.neurotech.callibrineurosdkdemo.screens.search

import com.neurosdk2.neuro.types.SensorInfo

data class DeviceListItem (val name: String, val address:String, var inProgress: Boolean, val sInfo: SensorInfo)