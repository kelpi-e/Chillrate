package com.neurotech.callibrineurosdkdemo.screens.ecg

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.neurotech.callibrineurosdkdemo.R
import com.neurotech.callibrineurosdkdemo.callibri.CallibriController
import com.neurotech.callibrineurosdkdemo.databinding.FragmentECGBinding
import com.neurotech.callibrineurosdkdemo.databinding.FragmentSpectrumBinding
import com.neurotech.callibrineurosdkdemo.screens.spectrum.SpectrumViewModel
import com.neurotech.callibrineurosdkdemo.utils.PlotHolder

class ECGFragment : Fragment() {

    companion object {
        fun newInstance() = ECGFragment()
    }

    private var _binding: FragmentECGBinding? = null
    private var _viewModel: ECGViewModel? = null

    private val binding get() = _binding!!
    private val viewModel get() = _viewModel!!

    private var plot: PlotHolder? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentECGBinding.inflate(inflater, container, false)
        _viewModel = ViewModelProvider(this)[ECGViewModel::class.java]

        binding.viewModel = viewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ecgStartButton.setOnClickListener { viewModel.onStartClicked() }

        plot = PlotHolder(binding.plotSignal)

        CallibriController.samplingFrequency?.let { plot?.startRender(it, PlotHolder.ZoomVal.V_AUTO_M_S2, 1.5f) }//побольше по x

        val samplesObserver = Observer<List<Double>> { newSamples ->
            plot?.addData(newSamples)
        }
        viewModel.samples.observe(requireActivity(), samplesObserver)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

        viewModel.close()
    }


}