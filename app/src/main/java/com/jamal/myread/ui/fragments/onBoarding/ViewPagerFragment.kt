package com.jamal.myread.ui.fragments.onBoarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.jamal.myread.databinding.FragmentViewPagerBinding
import com.jamal.myread.ui.adapters.ViewPagerAdapter

class ViewPagerFragment : Fragment() {
    private var _binding: FragmentViewPagerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentViewPagerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentListOnBoarding = arrayListOf(
            GetStartedOnBoardingFragment(),
            StartServiceOnBoardingFragment(),
            ReadItemOnBoardingFragment(),
            VoiceSettingsOnBoardingFragment()
        )

        val adapterOnBoardingViewPager = ViewPagerAdapter(
            fragmentListOnBoarding,
            requireActivity().supportFragmentManager,
            lifecycle
        )

        binding.viewPager.adapter = adapterOnBoardingViewPager
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}