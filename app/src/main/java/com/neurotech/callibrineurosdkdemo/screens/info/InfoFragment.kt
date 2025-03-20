package com.neurotech.callibrineurosdkdemo.screens.info

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.neurotech.callibrineurosdkdemo.databinding.FragmentInfoBinding

class InfoFragment : Fragment() {

    companion object {
        fun newInstance() = InfoFragment()
    }

    private var _binding: FragmentInfoBinding? = null
    private var _viewModel: InfoViewModel? = null

    private val binding get() = _binding!!
    private val viewModel get() = _viewModel!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInfoBinding.inflate(inflater, container, false)
        _viewModel = ViewModelProvider(this)[InfoViewModel::class.java]

        binding.viewModel = viewModel

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}