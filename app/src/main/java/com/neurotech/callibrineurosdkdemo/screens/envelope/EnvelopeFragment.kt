package com.neurotech.callibrineurosdkdemo.screens.envelope

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.neurotech.callibrineurosdkdemo.R
import com.neurotech.callibrineurosdkdemo.callibri.CallibriController
import com.neurotech.callibrineurosdkdemo.databinding.FragmentEnvelopeBinding
import com.neurotech.callibrineurosdkdemo.databinding.FragmentSignalBinding
import com.neurotech.callibrineurosdkdemo.screens.signal.SignalViewModel
import com.neurotech.callibrineurosdkdemo.utils.PlotHolder

class EnvelopeFragment : Fragment() {

    companion object {
        fun newInstance() = EnvelopeFragment()
    }

    private var _binding: FragmentEnvelopeBinding? = null
    private var _viewModel: EnvelopeViewModel? = null

    private val binding get() = _binding!!
    private val viewModel get() = _viewModel!!

    private var plot: PlotHolder? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEnvelopeBinding.inflate(inflater, container, false)
        _viewModel = ViewModelProvider(this)[EnvelopeViewModel::class.java]

        binding.viewModel = viewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.envelopeButton.setOnClickListener { viewModel.onStartClicked() }

        plot = PlotHolder(binding.plotSignal)
        plot?.startRender(40.0f, PlotHolder.ZoomVal.V_AUTO_M_S2, 5.0f)

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