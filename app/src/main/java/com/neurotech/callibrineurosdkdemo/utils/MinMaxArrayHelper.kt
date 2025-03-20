package com.neurotech.callibrineurosdkdemo.utils

import java.util.*


class MinMaxArrayHelper(var arraySize: Int) {
    private val _data: Array<Number?>
    private val _dataOrdered: SortedSet<Number?>
    private val _dataLen: Int
    private var _latestIndex = 0

    init {
        if (arraySize <= 0) arraySize = 1
        _dataLen = arraySize
        _data = arrayOfNulls(arraySize)
        for (i in 0 until arraySize) _data[i] = 0.0
        _dataOrdered = TreeSet(object : Comparator<Number?> {
            override fun compare(p0: Number?, p1: Number?): Int {
                return if (p0 != p1) java.lang.Double.compare(p0!!.toDouble(), p1!!.toDouble()) else 0
            }
        })
    }

    fun addValue(`val`: Number?) {
        _dataOrdered.remove(_data[_latestIndex])
        _data[_latestIndex] = `val`
        _dataOrdered.add(`val`)
        _latestIndex = (_latestIndex + 1) % _dataLen
    }

    val min: Number?
        get() = _dataOrdered.first()
    val max: Number?
        get() = _dataOrdered.last()
}