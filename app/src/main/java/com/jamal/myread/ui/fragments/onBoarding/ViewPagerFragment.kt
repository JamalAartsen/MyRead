package com.jamal.myread.ui.fragments.onBoarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.jamal.myread.databinding.FragmentViewPagerBinding
import com.jamal.myread.ui.adapters.ViewPagerAdapter
import com.jamal.myread.viewmodel.NavigateViewPagerViewModel

class ViewPagerFragment : Fragment() {
    private var _binding: FragmentViewPagerBinding? = null
    private val binding get() = _binding!!
    private val navigateViewPagerViewModel: NavigateViewPagerViewModel by viewModels()

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
            childFragmentManager,
            lifecycle
        )

        binding.viewPager.adapter = adapterOnBoardingViewPager

        navigateViewPagerViewModel.item.observe(viewLifecycleOwner) {
            binding.viewPager.setCurrentItem(it, true)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}