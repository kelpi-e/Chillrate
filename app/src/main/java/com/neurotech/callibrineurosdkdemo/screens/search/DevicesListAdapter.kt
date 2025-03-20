package com.neurotech.callibrineurosdkdemo.screens.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.neurosdk2.neuro.types.SensorInfo
import com.neurotech.callibrineurosdkdemo.databinding.SearchDeviceListItemBinding

class DevicesListAdapter(var devices: MutableList<DeviceListItem>) : RecyclerView.Adapter<DevicesListAdapter.ViewHolder>() {

    private lateinit var binding: SearchDeviceListItemBinding

    class ViewHolder(var binding: SearchDeviceListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(device: DeviceListItem) {
            binding.deviceListItem = device
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = SearchDeviceListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val device = devices[position]
        holder.bind(device)
    }

    override fun getItemCount() = devices.size

}