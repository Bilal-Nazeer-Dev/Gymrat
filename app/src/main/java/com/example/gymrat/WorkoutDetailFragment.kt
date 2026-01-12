package com.example.gymrat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.gymrat.databinding.FragmentWorkoutDetailBinding

class WorkoutDetailFragment : Fragment() {

    private var _binding: FragmentWorkoutDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkoutDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Get the Data passed from the List Screen
        // We use "arguments" to retrieve the Serializable object
        val workout = arguments?.getSerializable("workout_data") as? WorkoutPlan

        if (workout != null) {
            // 2. Bind Data to UI
            binding.tvDetailTitle.text = workout.title
            binding.chipDifficulty.text = workout.difficulty
            binding.chipDuration.text = workout.duration
            binding.tvDetailDescription.text = workout.description
            binding.ivDetailImage.setImageResource(workout.imageRes)
        }

        // 3. Back Button Logic
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        // 4. Start Button Logic
        binding.btnStartWorkout.setOnClickListener {
            // Pass the same workout data to the active screen
            val bundle = Bundle().apply {
                putSerializable("workout_data", workout)
            }
            findNavController().navigate(R.id.action_workoutDetailFragment_to_activeWorkoutFragment, bundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}