package com.example.gymrat

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.gymrat.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    // CRASH FIX: We do not use 'binding!!' anymore to prevent NullPointerException

    // Firestore instance
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Listener variable to clean up later
    private var userListener: ListenerRegistration? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Setup Navigation
        setupNavigation()

        // 2. Start Listening to Real-Time Data
        // We only READ data here. We do NOT write/increment streak here (that happens in AttendanceFragment).
        listenToUserData()
    }

    private fun setupNavigation() {
        // Safe check for binding before accessing views
        val bind = _binding ?: return

        // Profile Button
        bind.btnProfile.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
        }

        // Attendance Card
        bind.cardAttendance.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_attendanceFragment)
        }

        // BMI Card
        bind.cardBMI.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_bmiFragment)
        }

        // Workouts Card
        bind.cardWorkouts.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_workoutsFragment)
        }

        // Diet Card
        bind.cardDiet.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_dietFragment)
        }

        // AI Bot Card
        bind.cardAI.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_chatFragment)
        }
    }

    // --- REAL-TIME LISTENER (Reads data and updates UI safely) ---
    private fun listenToUserData() {
        val userId = auth.currentUser?.uid ?: return

        userListener = db.collection("users").document(userId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("HomeFragment", "Listen failed.", e)
                    return@addSnapshotListener
                }

                // CRASH FIX: Check if _binding is null before updating the UI
                // This prevents the app from crashing if the user leaves the screen quickly
                if (_binding != null && snapshot != null && snapshot.exists()) {

                    // A. Update Name
                    val name = snapshot.getString("user_name")
                    if (!name.isNullOrEmpty()) {
                        _binding?.tvUsername?.text = "$name!"
                    } else {
                        _binding?.tvUsername?.text = "Gym Rat!"
                    }

                    // B. Update Streak Display
                    val streak = snapshot.getLong("streak") ?: 0
                    _binding?.tvStreakCount?.text = "$streak Days"
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Stop the listener to save battery and prevent memory leaks
        userListener?.remove()
        _binding = null
    }
}