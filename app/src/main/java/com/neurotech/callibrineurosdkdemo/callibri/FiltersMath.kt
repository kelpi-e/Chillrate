package com.neurotech.callibrineurosdkdemo.callibri

import com.neurotech.filters.*

class FiltersMath {

    val filtersLP = mutableMapOf<String, FilterParam>()
    val filtersHP = mutableMapOf<String, FilterParam>()
    val filtersBS = mutableMapOf<String, FilterParam>()
    private val preinstalledFilterList = PreinstalledFilterList.get()

    init {
        val callibriSF = CallibriController.samplingFrequency?.toInt()
        for (pf in preinstalledFilterList){
            if(pf.samplingFreq != callibriSF) continue

            if(pf.type == FilterType.FtBandPass || pf.type == FilterType.FtBandStop){
                filtersBS["${pf.cutoffFreq} Hz, ${pf.samplingFreq} Hz"] = pf
            }
            if(pf.type == FilterType.FtHP){
                filtersHP["${pf.cutoffFreq} Hz, ${pf.samplingFreq} Hz"] = pf
            }
            if(pf.type == FilterType.FtLP){
                filtersLP["${pf.cutoffFreq} Hz, ${pf.samplingFreq} Hz"] = pf
            }
        }
    }

    private val tmpFilterList = mutableMapOf<FilterParam, Int>()
    private val filterList: FilterList = FilterList()

    fun addFilter(fParam: FilterParam){
        val f = Filter(fParam)
        tmpFilterList[fParam] = f.id
        filterList.addFilter(f)
    }

    fun removeFilter(fParam: FilterParam){
        tmpFilterList[fParam]?.let { filterList.deleteFilter(it) }
    }

    fun filter(samples:DoubleArray): DoubleArray{
        return filterList.filterArray(samples)
    }


}