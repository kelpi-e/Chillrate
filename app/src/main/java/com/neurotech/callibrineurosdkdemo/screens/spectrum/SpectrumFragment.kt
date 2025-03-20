package com.neurotech.callibrineurosdkdemo.screens.spectrum

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.androidplot.xy.LineAndPointFormatter
import com.androidplot.xy.SimpleXYSeries
import com.androidplot.xy.XYPlot
import com.neurotech.callibrineurosdkdemo.R
import com.neurotech.callibrineurosdkdemo.databinding.FragmentSpectrumBinding

class SpectrumFragment : Fragment() {

    companion object {
        fun newInstance() = SpectrumFragment()
    }

    private var _binding: FragmentSpectrumBinding? = null
    private var _viewModel: SpectrumViewModel? = null

    private val binding get() = _binding!!
    private val viewModel get() = _viewModel!!

    private lateinit var plot: XYPlot

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?

    ): View {
        _binding = FragmentSpectrumBinding.inflate(inflater, container, false)
        _viewModel = ViewModelProvider(this)[SpectrumViewModel::class.java]

        binding.viewModel = viewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.startSpectrum.setOnClickListener { viewModel.onStartClicked() }

        plot = binding.plotSpectrum

        val formatter = LineAndPointFormatter(context, R.xml.line_point_formatter)
        formatter.isLegendIconEnabled = false

        val spectrumObserver = Observer<List<Double>> {
            plot.clear()

            plot.addSeries(
                SimpleXYSeries(it, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Signal"), formatter
            )

            plot.redraw()
        }
        viewModel.spectrumBins.observe(requireActivity(), spectrumObserver)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

        viewModel.close()
    }

}