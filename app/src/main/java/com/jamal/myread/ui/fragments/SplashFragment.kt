package com.jamal.myread.ui.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.jamal.myread.R
import com.jamal.myread.databinding.FragmentSplashBinding
import com.jamal.myread.sharedpreferences.SharedPreferenceOnBoarding
import com.jamal.myread.sharedpreferences.SharedPreferencesKeys
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.bounce)
        animation.duration = 500
        binding.imageViewSplash?.startAnimation(animation)

        Handler(Looper.getMainLooper()).postDelayed({
            viewLifecycleOwner.lifecycleScope.launch {
                if (SharedPreferenceOnBoarding.getPreferences(
                        requireActivity(),
                        SharedPreferencesKeys.ON_BOARDING_IS_FINISHED
                    )
                ) {
                    findNavController().navigate(R.id.action_mainFragment_to_homeFragment)
                } else {
                    findNavController().navigate(R.id.action_mainFragment_to_viewPagerFragment)
                }
            }
        }, 1000)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}