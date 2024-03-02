package com.dtu.deltecdtu.fragments

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabsIntent
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.dtu.deltecdtu.R
import com.dtu.deltecdtu.databinding.FragmentPrivacyPolicyBinding
import com.example.deltecdtu.Util.Constants.Companion.TIME_FOR_FRAGMENT
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PrivacyPolicyFragment : Fragment() {

    private var _binding: FragmentPrivacyPolicyBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPrivacyPolicyBinding.inflate(inflater, container, false)

        lifecycleScope.launch {
            delay(TIME_FOR_FRAGMENT)
            val aboutUrl = "https://sites.google.com/view/deltec-dtu-privacy-policy/home"
            val intent = CustomTabsIntent.Builder()
                .build()
            intent.launchUrl(requireContext(), Uri.parse(aboutUrl))
            findNavController().popBackStack()
        }
        return binding.root
    }
}