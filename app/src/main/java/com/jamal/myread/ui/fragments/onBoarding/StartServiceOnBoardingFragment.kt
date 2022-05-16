package com.jamal.myread.ui.fragments.onBoarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.jamal.myread.databinding.FragmentStartServiceOnboardingBinding
import com.jamal.myread.setSizesOnBoarding
import com.jamal.myread.viewmodel.NavigateViewPagerViewModel

class StartServiceOnBoardingFragment : Fragment() {
    private var _binding: FragmentStartServiceOnboardingBinding? = null
    private val binding get() = _binding!!
    private val navigateViewPagerViewModel: NavigateViewPagerViewModel by viewModels(
        ownerProducer = { requireParentFragment() }
    )

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

        binding.run {
            btnNextStartService.setOnClickListener {
                navigateViewPagerViewModel.navigateTo(2)
            }
            btnBackStartService.setOnClickListener {
                navigateViewPagerViewModel.navigateTo(0)
            }
        }

        val sizes = setSizesOnBoarding(resources)

        binding.run {
            if (sizes != null) {
                introductionTitle.textSize = sizes.titleSize
                introductionText.textSize = sizes.descriptionSize
                helloPerson.layoutParams.width = sizes.imageSize
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}