package com.neurotech.callibrineurosdkdemo.screens.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.neurosdk2.neuro.types.SensorState
import com.neurotech.callibrineurosdkdemo.R
import com.neurotech.callibrineurosdkdemo.callibri.CallibriController
import com.neurotech.callibrineurosdkdemo.databinding.FragmentMenuBinding

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class MenuFragment : Fragment() {

    private var _binding: FragmentMenuBinding? = null
    private var _viewModel: MenuViewModel? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val viewModel get() = _viewModel!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMenuBinding.inflate(inflater, container, false)
        _viewModel = ViewModelProvider(this).get(MenuViewModel::class.java)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewModel = viewModel

        binding.buttonSearch.setOnClickListener {
            findNavController().navigate(R.id.action_MenuFragment_to_SearchFragment)
        }

        binding.buttonSignal.setOnClickListener {
            if(CallibriController.connectionState == SensorState.StateInRange){
                findNavController().navigate(R.id.action_MenuFragment_to_signalFragment)
            }
            else {
                Toast.makeText(requireActivity(), "Connect to device first!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.buttonInfo.setOnClickListener {
            if(CallibriController.connectionState == SensorState.StateInRange){
                findNavController().navigate(R.id.action_MenuFragment_to_infoFragment)
            }
            else {
                Toast.makeText(requireActivity(), "Connect to device first!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.buttonEnvelope.setOnClickListener {
            if(CallibriController.connectionState == SensorState.StateInRange){
                findNavController().navigate(R.id.action_MenuFragment_to_envelopeFragment)
            }
            else {
                Toast.makeText(requireActivity(), "Connect to device first!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.buttonSpectrum.setOnClickListener {
            findNavController().navigate(R.id.action_MenuFragment_to_spectrumFragment)
        }

        binding.buttonEcg.setOnClickListener {
            findNavController().navigate(R.id.action_MenuFragment_to_ECGFragment)
        }

        binding.buttonEmotions.setOnClickListener {
            findNavController().navigate(R.id.action_MenuFragment_to_emotionsFragment)
        }

        binding.buttonCurrentReconect.setOnClickListener {
            if(CallibriController.hasDevice) viewModel.reconnect()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()

        viewModel.updateDeviceInfo()
    }
}