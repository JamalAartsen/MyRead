package com.jamal.myread.ui.fragments.onBoarding

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.jamal.myread.R
import com.jamal.myread.databinding.FragmentReadItemOnboardingBinding
import com.jamal.myread.setSizesOnBoarding
import com.jamal.myread.viewmodel.NavigateViewPagerViewModel

class ReadItemOnBoardingFragment : Fragment() {
    private var _binding: FragmentReadItemOnboardingBinding? = null
    private val binding get() = _binding!!
    private val navigateViewPagerViewModel: NavigateViewPagerViewModel by viewModels(
        ownerProducer = { requireParentFragment() }
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReadItemOnboardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.run {
            btnNextReadItem.setOnClickListener {
                navigateViewPagerViewModel.navigateTo(3)
            }
            btnBackReadItem.setOnClickListener {
                navigateViewPagerViewModel.navigateTo(1)
            }
        }

        val sizes = setSizesOnBoarding(resources)

        binding.run {
            if (sizes != null) {
                readItemTitle.textSize = sizes.titleSize
                readItemText.textSize = sizes.descriptionSize
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}