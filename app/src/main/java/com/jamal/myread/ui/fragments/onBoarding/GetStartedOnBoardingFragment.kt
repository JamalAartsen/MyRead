package com.jamal.myread.ui.fragments.onBoarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.viewpager2.widget.ViewPager2
import com.jamal.myread.R
import com.jamal.myread.databinding.FragmentGetStartedOnboardingBinding

class GetStartedOnBoardingFragment : Fragment(R.layout.fragment_get_started_onboarding) {

    private var _binding: FragmentGetStartedOnboardingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGetStartedOnboardingBinding.inflate(inflater, container, false)
        requireActivity().window.navigationBarColor = ContextCompat.getColor(
            requireContext(),
            R.color.light_purple
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewPager = activity?.findViewById<ViewPager2>(R.id.viewPager)
        val navHostFragment =
            requireActivity().supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment

        val graphInflater = navHostFragment.navController.navInflater
        val navGraph = graphInflater.inflate(R.navigation.main_navigation_graph)

        binding.introGetStartedBtn.setOnClickListener {
            viewPager?.currentItem = 1
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}