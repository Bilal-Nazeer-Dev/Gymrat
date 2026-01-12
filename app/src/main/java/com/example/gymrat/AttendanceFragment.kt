package com.example.gymrat

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController // <--- 1. IMPORT ADDED
import com.example.gymrat.databinding.FragmentAttendanceBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AttendanceFragment : Fragment() {

    private var _binding: FragmentAttendanceBinding? = null
    // We will NOT use the 'binding' property in callbacks to avoid crashes
    private val binding get() = _binding!!

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private var streakListener: ListenerRegistration? = null
    private var attendanceListener: ListenerRegistration? = null

    private val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAttendanceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // --- 2. BACK BUTTON LOGIC ---
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        // 3. Setup Live Data
        setupRealTimeListeners()

        // 4. Handle Click
        binding.btnMarkAttendance.setOnClickListener {
            markAttendanceTransaction()
        }
    }

    private fun setupRealTimeListeners() {
        val userId = auth.currentUser?.uid ?: return

        // Listener A: Update Streak Number
        streakListener = db.collection("users").document(userId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) return@addSnapshotListener

                // SAFE CHECK: Only update if view exists
                val currentBinding = _binding
                if (currentBinding != null && snapshot != null && snapshot.exists()) {
                    val streak = snapshot.getLong("streak") ?: 0
                    currentBinding.tvStreakCount.text = "$streak"
                }
            }

        // Listener B: Check if Button should be disabled
        attendanceListener = db.collection("users").document(userId)
            .collection("attendance")
            .document(todayDate)
            .addSnapshotListener { snapshot, e ->
                if (e != null) return@addSnapshotListener

                // SAFE CHECK
                if (_binding != null) {
                    if (snapshot != null && snapshot.exists()) {
                        showMarkedState()
                    } else {
                        showUnmarkedState()
                    }
                }
            }
    }

    private fun markAttendanceTransaction() {
        val userId = auth.currentUser?.uid ?: return

        // SAFE CHECK
        if (_binding == null) return

        setLoading(true)

        val userRef = db.collection("users").document(userId)
        val attendanceRef = userRef.collection("attendance").document(todayDate)

        db.runTransaction { transaction ->
            val attendanceSnapshot = transaction.get(attendanceRef)

            // 1. Double check inside transaction
            if (attendanceSnapshot.exists()) {
                throw FirebaseFirestoreException("Already marked", FirebaseFirestoreException.Code.ABORTED)
            }

            // 2. Get current streak safely
            val userSnapshot = transaction.get(userRef)
            val currentStreak = userSnapshot.getLong("streak") ?: 0

            // 3. Prepare updates
            val data = hashMapOf(
                "date" to todayDate,
                "timestamp" to System.currentTimeMillis()
            )

            // 4. Commit updates
            transaction.set(attendanceRef, data)
            transaction.update(userRef, "streak", currentStreak + 1)

            // Return the new value
            currentStreak + 1
        }.addOnSuccessListener { newStreak ->
            // CRASH FIX: Check _binding directly
            if (_binding != null) {
                setLoading(false)

                // 🔥 TRIGGER SUCCESS ANIMATION
                animateSuccess()
            }
        }.addOnFailureListener { e ->
            // CRASH FIX
            if (_binding != null) {
                setLoading(false)
                Log.e("Attendance", "Error marking attendance: ${e.message}")
            }
        }
    }

    private fun showMarkedState() {
        _binding?.progressBar?.visibility = View.GONE
        _binding?.btnMarkAttendance?.apply {
            text = "Attendance Marked ✅"
            isEnabled = false
            alpha = 0.7f
        }
        _binding?.tvStatus?.text = "You are done for today!"
    }

    private fun showUnmarkedState() {
        _binding?.progressBar?.visibility = View.GONE
        _binding?.btnMarkAttendance?.apply {
            text = "Mark Attendance"
            isEnabled = true
            alpha = 1.0f
        }
        _binding?.tvStatus?.text = "Keep the streak alive!"
    }

    private fun setLoading(isLoading: Boolean) {
        val currentBinding = _binding ?: return

        if (isLoading) {
            currentBinding.progressBar.visibility = View.VISIBLE
            currentBinding.btnMarkAttendance.text = ""
            currentBinding.btnMarkAttendance.isEnabled = false
        }
    }

    // 🔥 This function handles the Bounce Animation
    private fun animateSuccess() {
        val currentBinding = _binding ?: return

        currentBinding.btnMarkAttendance.animate()
            .scaleX(1.1f) // Scale UP to 110%
            .scaleY(1.1f)
            .setDuration(150)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    // Scale back DOWN to 100%
                    _binding?.btnMarkAttendance?.animate()
                        ?.scaleX(1.0f)
                        ?.scaleY(1.0f)
                        ?.setDuration(150)
                        ?.start()
                }
            })
            .start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        streakListener?.remove()
        attendanceListener?.remove()
        _binding = null
    }
}