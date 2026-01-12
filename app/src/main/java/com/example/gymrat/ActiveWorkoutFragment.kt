package com.example.gymrat

import android.content.Context
import android.os.SystemClock
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.gymrat.databinding.FragmentActiveWorkoutBinding

class ActiveWorkoutFragment : Fragment() {

    private var _binding: FragmentActiveWorkoutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentActiveWorkoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Get Data (Title)
        val workout = arguments?.getSerializable("workout_data") as? WorkoutPlan
        if (workout != null) {
            binding.tvWorkoutTitle.text = workout.title
        }

        // 2. Start Timer
        binding.chronometer.base = SystemClock.elapsedRealtime()
        binding.chronometer.start()

        // 3. Finish Button Logic
        binding.btnFinishWorkout.setOnClickListener {
            binding.chronometer.stop()

            // --- FEATURE: Update Profile Stats ---
            incrementWorkoutCount()

            Toast.makeText(context, "Workout Completed! 💪", Toast.LENGTH_SHORT).show()

            // Go back to Home
            findNavController().navigate(R.id.action_activeWorkoutFragment_to_homeFragment)
        }

        // 4. Back Button Logic (MOVED HERE)
        binding.btnBack.setOnClickListener {
            // Optional: Stop the chronometer before leaving
            binding.chronometer.stop()

            // Go back to the previous screen
            findNavController().navigateUp()
        }
    }

    // This helper function stays outside onViewCreated, but inside the Class
    private fun incrementWorkoutCount() {
        val sharedPref = activity?.getSharedPreferences("profile_pref", Context.MODE_PRIVATE) ?: return

        // Get current count (default 0)
        val currentCount = sharedPref.getInt("stats_workouts", 0)
        val newCount = currentCount + 1

        // Save new count
        with(sharedPref.edit()) {
            putInt("stats_workouts", newCount)
            apply()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}