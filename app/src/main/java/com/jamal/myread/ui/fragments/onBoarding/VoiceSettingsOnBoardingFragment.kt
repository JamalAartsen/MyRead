package com.jamal.myread.ui.fragments.onBoarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.jamal.myread.R
import com.jamal.myread.databinding.FragmentVoiceSettingsOnboardingBinding
import com.jamal.myread.utils.Constants
import com.jamal.myread.utils.SharedPreferenceOnBoarding
import com.jamal.myread.viewmodel.NavigateViewPagerViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VoiceSettingsOnBoardingFragment : Fragment() {
    private var _binding: FragmentVoiceSettingsOnboardingBinding? = null
    private val binding get() = _binding!!
    private val navigateViewPagerViewModel: NavigateViewPagerViewModel by viewModels(
        ownerProducer = { requireParentFragment() }
    )

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

        binding.run {
            btnNextVoiceSettings.setOnClickListener {
                findNavController()
                    .navigate(R.id.action_viewPagerFragment_to_homeFragment)
                SharedPreferenceOnBoarding.savePreferences(
                    requireActivity(),
                    Constants.ON_BOARDING_IS_FINISHED,
                    true
                )
            }
            btnBackVoiceSettings.setOnClickListener {
                navigateViewPagerViewModel.navigateTo(2)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}