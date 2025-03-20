package com.neurotech.callibrineurosdkdemo.screens.info

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import com.neurotech.callibrineurosdkdemo.callibri.CallibriController

class InfoViewModel : ViewModel() {
    var infoText = ObservableField(CallibriController.fullInfo())
}