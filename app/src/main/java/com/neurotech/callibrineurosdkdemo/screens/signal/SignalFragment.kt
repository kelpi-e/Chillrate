package com.neurotech.callibrineurosdkdemo.screens.signal

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import com.neurosdk2.neuro.types.CallibriSignalType
import com.neurotech.callibrineurosdkdemo.callibri.CallibriController
import com.neurotech.callibrineurosdkdemo.callibri.FiltersMath
import com.neurotech.callibrineurosdkdemo.databinding.FragmentSignalBinding
import com.neurotech.callibrineurosdkdemo.utils.PlotHolder
import com.neurotech.filters.FilterParam

class SignalFragment : Fragment() {

    companion object {
        fun newInstance() = SignalFragment()
    }

    private var _binding: FragmentSignalBinding? = null
    private var _viewModel: SignalViewModel? = null

    private val binding get() = _binding!!
    private val viewModel get() = _viewModel!!

    private var plot: PlotHolder? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignalBinding.inflate(inflater, container, false)
        _viewModel = ViewModelProvider(this)[SignalViewModel::class.java]

        binding.viewModel = viewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.signalButton.setOnClickListener { viewModel.onSignalClicked() }

        plot = PlotHolder(binding.plotSignal)
        CallibriController.samplingFrequency?.let { plot?.startRender(it, PlotHolder.ZoomVal.V_AUTO_M_S2, 5.0f) }

        val samplesObserver = Observer<List<Double>> { newSamples ->
            plot?.addData(newSamples)
        }
        viewModel.samples.observe(requireActivity(), samplesObserver)

        filtersSpinnersImpl()
    }

    private fun filtersSpinnersImpl(){
        val preinstallFilters = FiltersMath()

        val stringComparator = Comparator { first: String, second: String ->
            first.split('.').first().toInt() - second.split('.').first().toInt()
        }

        val lpArr = preinstallFilters.filtersLP.keys.toTypedArray().sortedWith(stringComparator)
        var prevLP: FilterParam? = preinstallFilters.filtersLP[lpArr[10]]
        ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            lpArr
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerLp.adapter = it
            binding.spinnerLp.setSelection(10)
            binding.spinnerLp.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    prevLP?.let { it1 -> viewModel.removeFilter(it1) }
                    prevLP = preinstallFilters.filtersLP[lpArr[position]]
                    preinstallFilters.filtersLP[lpArr[position]]?.let { it1 ->
                        viewModel.addFilter(
                            it1
                        )
                    }
                }
            }
        }
        prevLP?.let { viewModel.addFilter(it) }

        val hpArr = preinstallFilters.filtersHP.keys.toTypedArray().sortedWith(stringComparator)
        var prevHP: FilterParam? = preinstallFilters.filtersHP[hpArr[5]]
        ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            hpArr
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerHp.adapter = it
            binding.spinnerHp.setSelection(5)
            binding.spinnerHp.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    prevHP?.let { it1 -> viewModel.removeFilter(it1) }
                    prevHP = preinstallFilters.filtersHP[hpArr[position]]
                    preinstallFilters.filtersHP[hpArr[position]]?.let { it1 ->
                        viewModel.addFilter(
                            it1
                        )
                    }
                }
            }
        }
        prevHP?.let { viewModel.addFilter(it) }

        val bsArr = preinstallFilters.filtersBS.keys.toTypedArray().sortedWith(stringComparator)
        if(bsArr.isEmpty()) return
        var prevBS: FilterParam? = preinstallFilters.filtersBS[bsArr.first()]
        ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            bsArr
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerBs.adapter = it
            binding.spinnerBs.setSelection(0)
            binding.spinnerBs.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    prevBS?.let { it1 -> viewModel.removeFilter(it1) }
                    prevBS = preinstallFilters.filtersBS[bsArr[position]]
                    preinstallFilters.filtersBS[bsArr[position]]?.let { it1 ->
                        viewModel.addFilter(
                            it1
                        )
                    }
                }
            }
        }
        prevBS?.let { viewModel.addFilter(it) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

        viewModel.close()
    }

}