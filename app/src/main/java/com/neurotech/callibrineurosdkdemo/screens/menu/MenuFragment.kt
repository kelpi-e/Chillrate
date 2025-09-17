package com.neurotech.callibrineurosdkdemo.screens.menu

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.neurotech.callibrineurosdkdemo.MainActivity
import com.neurotech.callibrineurosdkdemo.R
import com.neurotech.callibrineurosdkdemo.callibri.CallibriController
import com.neurotech.callibrineurosdkdemo.databinding.FragmentMenuBinding
import com.neurotech.callibrineurosdkdemo.storage.HistoryStorage
import java.text.SimpleDateFormat
import java.util.*

class MenuFragment : Fragment() {
    private var _binding: FragmentMenuBinding? = null
    private var _viewModel: MenuViewModel? = null
    private val binding get() = _binding!!
    private val viewModel get() = _viewModel!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuBinding.inflate(inflater, container, false)
        _viewModel = ViewModelProvider(this).get(MenuViewModel::class.java)

        // Инициализируем хранилище (один раз, перед использованием)
        HistoryStorage.init(requireContext().applicationContext)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel

        // Кнопки переходов
        binding.buttonSearch.setOnClickListener {
            findNavController().navigate(R.id.action_MenuFragment_to_SearchFragment)
        }
        binding.buttonEcg.setOnClickListener {
            findNavController().navigate(R.id.action_MenuFragment_to_ECGFragment)
        }
        binding.buttonEmotions.setOnClickListener {
            findNavController().navigate(R.id.action_MenuFragment_to_emotionsFragment)
        }
        binding.buttonCurrentReconect.setOnClickListener {
            if (CallibriController.hasDevice) viewModel.reconnect()
        }

        // Кнопка логина
        binding.buttonLogin.setOnClickListener {
            val intent = Intent(requireActivity(), MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                putExtra("SHOW_LOGIN", true)
            }
            startActivity(intent)
            requireActivity().finish()
        }

        // Кнопка истории
        binding.buttonHistory.setOnClickListener {
            val entries = HistoryStorage.readLastHourEntries()
            if (entries.isEmpty()) {
                Toast.makeText(requireContext(), "History is empty for last hour", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val df = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            val sb = StringBuilder()
            for (e in entries) {
                sb.append(df.format(Date(e.ts)))
                    .append(" | relax(inst)=")
                    .append(String.format(Locale.getDefault(), "%.2f", e.instRelaxation))
                    .append("\n")
            }
            AlertDialog.Builder(requireContext())
                .setTitle("History (last hour)")
                .setMessage(sb.toString())
                .setPositiveButton("OK", null)
                .show()
        }

        // ✅ Новая кнопка: Сканировать код
        binding.buttonScanQr.setOnClickListener {
            findNavController().navigate(R.id.qrScannerFragment)
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
