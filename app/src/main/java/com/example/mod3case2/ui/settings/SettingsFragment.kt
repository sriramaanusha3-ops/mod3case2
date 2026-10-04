package com.example.mod3case2.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.mod3case2.R
import com.example.mod3case2.databinding.FragmentSettingsBinding
import com.example.mod3case2.ui.viewmodel.SettingsViewModel

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        setupListeners()
    }

    private fun setupObservers() {
        viewModel.currentBaseUrl.observe(viewLifecycleOwner) { url ->
            if (binding.etBaseUrl.text.toString() != url) {
                binding.etBaseUrl.setText(url)
            }
        }

        viewModel.connectionStatus.observe(viewLifecycleOwner) { status ->
            binding.tvConnectionStatus.text = status
            if (status.contains("ONLINE")) {
                binding.tvConnectionStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.green_emerald))
            } else if (status.contains("Connecting")) {
                binding.tvConnectionStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.cyan_accent))
            } else {
                binding.tvConnectionStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.red_coral))
            }
        }

        viewModel.isConnecting.observe(viewLifecycleOwner) { connecting ->
            binding.btnTestConnection.isEnabled = !connecting
        }
    }

    private fun setupListeners() {
        binding.btnTestConnection.setOnClickListener {
            val url = binding.etBaseUrl.text.toString().trim()
            if (url.isNotEmpty()) {
                viewModel.updateBaseUrl(url)
            } else {
                Toast.makeText(requireContext(), "Please enter a valid backend URL", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
