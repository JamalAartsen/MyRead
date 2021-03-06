package com.jamal.myread.ui.fragments.onBoarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.jamal.myread.R
import com.jamal.myread.databinding.FragmentGetStartedOnboardingBinding
import com.jamal.myread.utils.setSizesOnBoarding
import com.jamal.myread.viewmodel.NavigateViewPagerViewModel

class GetStartedOnBoardingFragment : Fragment() {

    private var _binding: FragmentGetStartedOnboardingBinding? = null
    private val binding get() = _binding!!
    private val navigateViewPagerViewModel: NavigateViewPagerViewModel by viewModels(
        ownerProducer = { requireParentFragment() }
    )

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

        binding.introGetStartedBtn.setOnClickListener {
            navigateViewPagerViewModel.navigateTo(1)
        }

        val sizes = setSizesOnBoarding(resources)

        binding.run {
            if (sizes != null) {
                introTitle.textSize = sizes.titleSize
                introSubtitle.textSize = sizes.descriptionSize
                readPersonIntro.layoutParams.width = sizes.imageSize
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}