package com.neurotech.callibrineurosdkdemo.screens.emotions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.neurotech.callibrineurosdkdemo.databinding.FragmentEmotionsBinding

class EmotionsFragment : Fragment() {
    private lateinit var binding: FragmentEmotionsBinding
    private lateinit var viewModel: EmotionsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEmotionsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[EmotionsViewModel::class.java]

        binding.signalButton.setOnClickListener { viewModel.onSignalClicked() }

        binding.viewModel = viewModel
    }

    override fun onDestroyView() {
        super.onDestroyView()

        viewModel.close()
    }
}
