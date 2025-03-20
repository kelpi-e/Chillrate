package com.neurotech.callibrineurosdkdemo.screens.search

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.neurosdk2.helpers.PermissionHelper
import com.neurosdk2.neuro.types.SensorState
import com.neurotech.callibrineurosdkdemo.R
import com.neurotech.callibrineurosdkdemo.callibri.CallibriController
import com.neurotech.callibrineurosdkdemo.databinding.FragmentSearchScreenBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class SearchScreenFragment : Fragment() {

    private var _binding: FragmentSearchScreenBinding? = null
    private var _viewModel: SearchScreenViewModel? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val viewModel get() = _viewModel!!

    private val devicesListAdapter = DevicesListAdapter(mutableListOf())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchScreenBinding.inflate(inflater, container, false)
        _viewModel = ViewModelProvider(this).get(SearchScreenViewModel::class.java)

        return binding.root

    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewModel = viewModel

        val llm = LinearLayoutManager(context)
        llm.orientation = LinearLayoutManager.VERTICAL
        binding.searchDevicesList.layoutManager = llm
        binding.searchDevicesList.adapter = devicesListAdapter

        binding.searchDevicesList.setOnItemClickListener {
            Handler(Looper.getMainLooper()).post {
                val deviceNumber = it

                devicesListAdapter.devices[deviceNumber].inProgress = true
                devicesListAdapter.notifyItemChanged(deviceNumber)

                CallibriController.createAndConnect(
                    devicesListAdapter.devices[deviceNumber].sInfo,
                    onConnectionResult = {
                        Handler(Looper.getMainLooper()).post {

                            if (it == SensorState.StateInRange) {
                                findNavController().popBackStack(R.id.MenuFragment, false)
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "Device connection fail!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            devicesListAdapter.devices[deviceNumber].inProgress = false
                            devicesListAdapter.notifyItemChanged(deviceNumber)
                        }
                    })
            }

        }

        binding.buttonRestart.setOnClickListener {
            if (!PermissionHelper.HasAllPermissions(requireContext())) {
                PermissionHelper.RequestPermissions(
                    requireContext()
                ) { grantedPermissions, deniedPermissions, deniedPermanentlyPermissions -> viewModel.onSearchClicked() }
            } else {
                viewModel.onSearchClicked()
            }
        }

        viewModel.sensors.observe(viewLifecycleOwner) {
            devicesListAdapter.devices.clear()
            for (device in it) {
                devicesListAdapter.devices.add(
                    DeviceListItem(
                        device.name,
                        device.address,
                        false,
                        device
                    )
                )
            }
            devicesListAdapter.notifyDataSetChanged()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

        viewModel.close()
    }

    private inline fun RecyclerView.setOnItemClickListener(crossinline listener: (position: Int) -> Unit) {
        addOnItemTouchListener(
            RecyclerItemClickListener(this,
                object : RecyclerItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View, position: Int) {
                        listener(position)
                    }
                })
        )
    }
}