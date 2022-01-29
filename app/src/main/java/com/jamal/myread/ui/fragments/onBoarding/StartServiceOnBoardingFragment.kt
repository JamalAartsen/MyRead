package com.jamal.myread.ui.fragments.onBoarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.jamal.myread.R
import com.jamal.myread.databinding.FragmentStartServiceOnboardingBinding

class StartServiceOnBoardingFragment : Fragment(R.layout.fragment_start_service_onboarding) {
    private var _binding: FragmentStartServiceOnboardingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStartServiceOnboardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewPager = activity?.findViewById<ViewPager2>(R.id.viewPager)

        binding.btnNextStartService.setOnClickListener {
            viewPager?.currentItem = 2
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}