package com.jamal.myread.ui.fragments.onBoarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.viewpager2.widget.ViewPager2
import com.jamal.myread.R
import com.jamal.myread.databinding.FragmentReadItemOnboardingBinding

class ReadItemOnBoardingFragment : Fragment() {
    private var _binding: FragmentReadItemOnboardingBinding? = null
    private val binding get() = _binding!!

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

        val viewPager = activity?.findViewById<ViewPager2>(R.id.viewPager)

        binding.btnFinishReadItem.setOnClickListener {
            viewPager?.currentItem = 3
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}