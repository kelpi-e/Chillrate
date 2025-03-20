package com.neurotech.callibrineurosdkdemo.utils

import android.graphics.Color
import android.graphics.Paint
import com.androidplot.ui.HorizontalPositioning
import com.androidplot.ui.Size
import com.androidplot.ui.SizeMode
import com.androidplot.ui.VerticalPositioning
import com.androidplot.util.Redrawer
import com.androidplot.xy.AdvancedLineAndPointRenderer
import com.androidplot.xy.BoundaryMode
import com.androidplot.xy.XYPlot
import com.androidplot.xy.XYSeries
import java.lang.ref.WeakReference
import java.util.concurrent.atomic.AtomicReference
import kotlin.math.ceil



class PlotHolder(plotSignal: XYPlot?) {
    private val _plotSignal: XYPlot
    private var _plotSeries: SignalDoubleModel? = null
    private var _zoomVal: ZoomVal? = null

    init {
        if (plotSignal == null) throw NullPointerException("plotSignal can not be null")
        _plotSignal = plotSignal
        initPlot()
    }

    fun startRender(samplingFrequency:Float, zoomVal: ZoomVal, windowDurationSek: Float) {
        stopRender()
        val wndSizeSek = if (windowDurationSek <= 0) 5.0f else windowDurationSek
        val size = ceil((samplingFrequency * wndSizeSek).toDouble()).toInt()
        _plotSeries = SignalDoubleModel(
            size,
            samplingFrequency,
            zoomVal.isAuto,
            zoomVal.top.toDouble()
        )
        val formatter = SignalFadeFormatter(size)
        formatter.isLegendIconEnabled = false
        _plotSignal.addSeries(_plotSeries, formatter)
        setZoomY(zoomVal)

        _plotSignal.setDomainBoundaries(0, wndSizeSek, BoundaryMode.FIXED)
        _plotSeries!!.setRenderRef(
            WeakReference(
                _plotSignal.getRenderer(
                    AdvancedLineAndPointRenderer::class.java
                )
            )
        )
    }

    fun addData(data: List<Double>){
        // DoubleArray
        val ser = _plotSeries
        ser?.addData(data.toDoubleArray())
    }

    fun setZoomY(zoomVal: ZoomVal?) {
        if (zoomVal == null) return
        if (_zoomVal != zoomVal) {
            _zoomVal = zoomVal
            if (zoomVal.ordinal <= ZoomVal.V_AUTO_1.ordinal) {
                _plotSeries!!.setAutoRange(false)
                _plotSignal.setRangeBoundaries(
                    zoomVal.bottom,
                    zoomVal.top,
                    if (zoomVal.isAuto) BoundaryMode.AUTO else BoundaryMode.FIXED
                )
            } else {
                _plotSeries!!.setAutoRangeScale(zoomVal.top.toDouble())
                _plotSeries!!.setAutoRange(true)
            }
        }
    }

    fun zoomYIn() {
        val idx = _zoomVal!!.ordinal + 1
        val zValues = ZoomVal.values()
        if (idx < zValues.size) {
            setZoomY(zValues[idx])
        }
    }

    fun zoomYOut() {
        val idx = _zoomVal!!.ordinal - 1
        val zValues = ZoomVal.values()
        if (idx >= 0) {
            setZoomY(zValues[idx])
        }
    }

    fun stopRender() {
        if (_plotSeries != null) {
            _plotSignal.removeSeries(_plotSeries)
            _plotSeries = null
        }
    }

    private fun initPlot() {
        _plotSignal.graph.gridBackgroundPaint.color = Color.TRANSPARENT
        _plotSignal.backgroundPaint.color = Color.TRANSPARENT
        _plotSignal.graph.backgroundPaint.color = Color.TRANSPARENT
        _plotSignal.borderPaint.color = Color.TRANSPARENT
        _plotSignal.graph.size = Size(0F, SizeMode.FILL, 0F, SizeMode.FILL)
        _plotSignal.graph.position(
            0f,
            HorizontalPositioning.ABSOLUTE_FROM_LEFT,
            0f,
            VerticalPositioning.ABSOLUTE_FROM_TOP
        )
        _plotSignal.linesPerRangeLabel = 3
        Redrawer(_plotSignal, 30F, true)
    }

    private class SignalDoubleModel(
        size: Int,
        freqHz: Float,
        autoRange: Boolean,
        autoRangeScale: Double
    ) :
        XYSeries {
        private val _data: Array<DoubleArray>
        private var _rendererRef: WeakReference<AdvancedLineAndPointRenderer>? = null
        private var _latestIndex: Int
        private val _xStep: Double
        private val _dataSize: Int
        private val _minMaxYHelper: MinMaxArrayHelper
        private var _minYLast: Number? = null
        private var _maxYLast: Number? = null
        private var _autoRange: Boolean
        private val _autoRangeScale = AtomicReference(0.0)

        init {
            _data = Array(size) { DoubleArray(2) }
            _dataSize = size
            _xStep = 1.0 / freqHz
            for (i in 0 until _dataSize) {
                _data[i][0] = _xStep * i
            }
            _latestIndex = 0
            _minMaxYHelper = MinMaxArrayHelper(size)
            _autoRange = autoRange
            _autoRangeScale.set(autoRangeScale)
        }

        fun setRenderRef(rendererRef: WeakReference<AdvancedLineAndPointRenderer>?) {
            _rendererRef = rendererRef
        }

        fun addData(data: DoubleArray?) {
            val render = _rendererRef!!.get()
            if (render == null || data == null || data.size <= 0) return
            var idx = _latestIndex
            var i = 0
            while (i < data.size) {
                _data[idx][0] = idx * _xStep
                _data[idx][1] = data[i]
                _minMaxYHelper.addValue(data[i])
                ++i
                idx = (idx + 1) % _dataSize
            }
            _latestIndex = idx
            if (_autoRange) {
                var rangeChanged = false
                val min: Number = _minMaxYHelper.min!!
                if (_minYLast == null || java.lang.Double.compare(
                        _minYLast!!.toDouble(),
                        min.toDouble()
                    ) != 0
                ) {
                    _minYLast = min
                    rangeChanged = true
                }
                val max: Number = _minMaxYHelper.max!!
                if (_maxYLast == null || java.lang.Double.compare(
                        _maxYLast!!.toDouble(),
                        max.toDouble()
                    ) != 0
                ) {
                    _maxYLast = max
                    rangeChanged = true
                }
                if (rangeChanged) {
                    val offset =
                        Math.abs(_maxYLast!!.toDouble() - _minYLast!!.toDouble()) * _autoRangeScale.get()
                    render.plot.setRangeBoundaries(
                        _minYLast!!.toDouble() - offset,
                        _maxYLast!!.toDouble() + offset,
                        BoundaryMode.FIXED
                    )
                }
            }
            render.setLatestIndex(_latestIndex)
        }

        override fun size(): Int {
            return _dataSize
        }

        override fun getX(index: Int): Number {
            return _data[index][0]
        }

        override fun getY(index: Int): Number {
            return _data[index][1]
        }

        override fun getTitle(): String {
            return "Signal"
        }

        fun setAutoRange(autoRange: Boolean) {
            _autoRange = autoRange
        }

        fun setAutoRangeScale(autoRangeScale: Double) {
            _autoRangeScale.set(autoRangeScale)
        }
    }

    private class SignalFadeFormatter internal constructor(private val _trailSize: Int) :
        AdvancedLineAndPointRenderer.Formatter() {
        override fun getLinePaint(thisIndex: Int, latestIndex: Int, seriesSize: Int): Paint {
            val offset: Int
            offset = if (thisIndex >= latestIndex) {
                latestIndex + (seriesSize - thisIndex)
            } else {
                latestIndex - thisIndex
            }
            val scale = 255f / _trailSize
            val alpha = (255 - offset * scale).toInt()
            linePaint.alpha = Math.max(alpha, 0)
            return linePaint
        }
    }

    enum class ZoomVal {
        V_3(3, -3), V_2(2, -2), V_1(1, -1), V_05(0.5, -0.5), V_02(0.2, -0.2), V_01(
            0.1,
            -0.1
        ),
        V_005(0.05, -0.05), V_002(0.02, -0.02), V_001(0.01, -0.01), V_0005(0.005, -0.005), V_0002(
            0.002,
            -0.002
        ),
        V_0001(0.001, -0.001), V_00005(0.0005, -0.0005), V_00002(0.0002, -0.0002), V_00001(
            0.0001,
            -0.0001
        ),
        V_000005(0.00005, -0.00005), V_000002(0.00002, -0.00002), V_000001(
            0.00001,
            -0.00001
        ),
        V_AUTO_1(1, -1, true), V_AUTO_M_S05(0.5, -1, false), V_AUTO_M_S2(
            2,
            -1,
            false
        ),
        V_AUTO_M_S2_5(2.5, -1, false), V_AUTO_M_S3(3, -1, false), V_AUTO_M_S5(
            5,
            -1,
            false
        ),
        V_AUTO_M_S7(7, -1, false), V_AUTO_M_S10(10, -1, false);

        constructor(top: Number, bottom: Number, auto: Boolean) {
            this.top = top
            this.bottom = bottom
            isAuto = auto
        }

        constructor(top: Number, bottom: Number) {
            this.top = top
            this.bottom = bottom
            isAuto = false
        }

        val top: Number
        val bottom: Number
        val isAuto: Boolean
    }
}
