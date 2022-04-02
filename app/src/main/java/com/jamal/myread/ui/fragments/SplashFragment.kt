package com.jamal.myread.ui.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.jamal.myread.R
import com.jamal.myread.databinding.FragmentSplashBinding
import com.jamal.myread.utils.DataStoreOnBoarding
import com.jamal.myread.utils.PreferencesKeys
import com.jamal.myread.viewmodel.NavigationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!
    private val navigationViewModel by activityViewModels<NavigationViewModel>()
    @Inject
    lateinit var dataStoreOnBoarding: DataStoreOnBoarding

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
            if (dataStoreOnBoarding.readOnBoardingPreference(requireContext(), PreferencesKeys.IS_ON_BOARDING_FINISHED)) {
                findNavController().navigate(R.id.action_mainFragment_to_homeFragment)
            } else {
                findNavController().navigate(R.id.action_mainFragment_to_viewPagerFragment)
            }
        }
        }, 1000)

//        navigationViewModel.navigation.observe(viewLifecycleOwner) {
//            when (it) {
//                NavigationItem.TO_HOME_FRAGMENT -> findNavController().navigate(R.id.action_mainFragment_to_homeFragment)
//                NavigationItem.TO_ON_BOARDING_FRAGMENT -> findNavController().navigate(R.id.action_mainFragment_to_viewPagerFragment)
//            }
//        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}