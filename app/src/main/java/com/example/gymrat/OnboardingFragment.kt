package com.example.gymrat

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.gymrat.databinding.FragmentOnboardingBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth

class OnboardingFragment : Fragment() {

    private var _binding: FragmentOnboardingBinding? = null
    private val binding get() = _binding!!
    private lateinit var onboardingAdapter: OnboardingAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Check Auto Login
        checkAutoLogin()

        // 2. Setup Data
        val onboardingItems = listOf(
            OnboardingItem(
                R.drawable.onboarding_1,
                "Welcome to Gymrat",
                "Your ultimate AI-powered companion for tracking workouts, nutrition, and crushing your fitness goals."
            ),
            OnboardingItem(
                R.drawable.onboarding_2,
                "Smart AI Coaching",
                "Get real-time advice, personalized workout plans, and meal suggestions from your AI coach."
            ),
            OnboardingItem(
                R.drawable.onboarding_3,
                "Track & Visualize",
                "Monitor your daily attendance, maintain streaks, and watch your progress grow with intuitive charts."
            )
        )

        // 3. Setup Adapter
        onboardingAdapter = OnboardingAdapter(onboardingItems)
        binding.viewPager.adapter = onboardingAdapter

        // 4. Attach Tabs (Dots)
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { _, _ ->
            // No text, just dots
        }.attach()

        // 5. Page Change Logic (Visibility Handling)
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                // Logic to hide/show Skip button and change Next text
                if (position == onboardingItems.lastIndex) {
                    binding.btnNext.text = "Get Started"
                    binding.btnSkip.visibility = View.INVISIBLE // Hide Skip on last screen
                } else {
                    binding.btnNext.text = "Next"
                    binding.btnSkip.visibility = View.VISIBLE   // Show Skip on other screens
                }
            }
        })

        // 6. Next Button Click
        binding.btnNext.setOnClickListener {
            if (binding.viewPager.currentItem < onboardingAdapter.itemCount - 1) {
                binding.viewPager.currentItem++
            } else {
                finishOnboarding()
            }
        }

        // 7. Skip Button Click
        binding.btnSkip.setOnClickListener {
            finishOnboarding()
        }
    }

    private fun checkAutoLogin() {
        val auth = FirebaseAuth.getInstance()
        // Added check to ensure view is still valid to avoid crashes if fragment detached
        if (auth.currentUser != null && auth.currentUser!!.isEmailVerified) {
            // Only navigate if we are currently added to the activity
            if (isAdded) {
                findNavController().navigate(R.id.action_onboardingFragment_to_homeFragment)
            }
        }
    }

    private fun finishOnboarding() {
        val sharedPref = activity?.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        sharedPref?.edit()?.putBoolean("onboarding_finished", true)?.apply()

        findNavController().navigate(R.id.action_onboardingFragment_to_loginFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}