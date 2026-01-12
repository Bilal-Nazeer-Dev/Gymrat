package com.example.gymrat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gymrat.databinding.FragmentWorkoutsBinding

class WorkoutsFragment : Fragment() {

    private var _binding: FragmentWorkoutsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkoutsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // --- 1. BACK BUTTON LOGIC ---
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        // --- 2. PREPARE DATA ---
        val workoutList = listOf(
            WorkoutPlan("Full Body Crush", "Beginner", "30 min", R.drawable.workout_full_body,
                "A perfect start. Do 3 rounds of:\n• 15 Jumping Jacks\n• 10 Pushups\n• 15 Squats\n• 30s Plank"),
            WorkoutPlan("HIIT Cardio Blast", "Advanced", "20 min", R.drawable.workout_hiit,
                "High Intensity Interval Training.\n• 40s Burpees\n• 20s Rest\n• 40s Mountain Climbers\n• 20s Rest\nRepeat 5 times."),
            WorkoutPlan("Upper Body Strength", "Intermediate", "45 min", R.drawable.workout_upper_body,
                "Focus on chest and arms.\n• Bench Press: 4x8\n• Pullups: 3x10\n• Overhead Press: 3x12"),
            WorkoutPlan("Leg Day Destruction", "Advanced", "50 min", R.drawable.workout_legs,
                "Never skip leg day.\n• Squats: 5x5\n• Lunges: 3x12\n• Leg Press: 4x10\n• Calf Raises: 4x20"),
            WorkoutPlan("Core & Abs Sculpt", "Beginner", "15 min", R.drawable.workout_abs,
                "Core blaster circuit.\n• Crunches: 20\n• Leg Raises: 15\n• Russian Twists: 20\n• Plank: 1 min"),
            WorkoutPlan("Morning Yoga Flow", "Beginner", "25 min", R.drawable.workout_yoga,
                "Wake up your body.\n• Sun Salutations\n• Warrior I & II\n• Downward Dog\n• Child's Pose"),
            WorkoutPlan("Chest & Triceps", "Intermediate", "40 min", R.drawable.workout_chest_triceps,
                "Push day focus.\n• Incline Dumbbell Press: 3x10\n• Cable Flys: 3x12\n• Tricep Dips: 3x12"),
            WorkoutPlan("Back & Biceps Builder", "Intermediate", "40 min", R.drawable.workout_back_biceps,
                "Pull day focus.\n• Deadlifts: 3x5\n• Lat Pulldowns: 3x10\n• Barbell Curls: 3x10"),
            WorkoutPlan("Glute Activation", "Intermediate", "30 min", R.drawable.workout_glute,
                "Booty builder.\n• Hip Thrusts: 4x10\n• Glute Bridges: 3x15\n• Kickbacks: 3x15"),
            WorkoutPlan("Endurance Run", "Advanced", "60 min", R.drawable.workout_endurance,
                "Steady state cardio.\n• 5 min warmup walk\n• 50 min moderate pace run\n• 5 min cooldown walk")
        )

        // --- 3. RECYCLERVIEW SETUP ---
        binding.rvWorkouts.layoutManager = LinearLayoutManager(context)
        binding.rvWorkouts.adapter = WorkoutAdapter(workoutList) { selectedPlan ->

            // Pass the workout object to the detail screen
            val bundle = Bundle().apply {
                putSerializable("workout_data", selectedPlan)
            }
            findNavController().navigate(R.id.action_workoutsFragment_to_workoutDetailFragment, bundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}