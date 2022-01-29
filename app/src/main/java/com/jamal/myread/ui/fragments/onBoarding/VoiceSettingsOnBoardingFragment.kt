package com.jamal.myread.ui.fragments.onBoarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.viewpager2.widget.ViewPager2
import com.jamal.myread.R
import com.jamal.myread.databinding.FragmentVoiceSettingsOnboardingBinding

class VoiceSettingsOnBoardingFragment : Fragment() {
    private var _binding: FragmentVoiceSettingsOnboardingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVoiceSettingsOnboardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnNextVoiceSettings.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(R.id.action_viewPagerFragment_to_homeFragment)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}