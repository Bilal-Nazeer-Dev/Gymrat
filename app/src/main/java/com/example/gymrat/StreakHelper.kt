package com.example.gymrat

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object StreakHelper {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Callback interface to return data to the UI
    interface StreakCallback {
        fun onStreakUpdated(streak: Int)
        fun onError(error: String)
    }

    fun updateStreak(callback: StreakCallback) {
        val user = auth.currentUser ?: return
        val userRef = db.collection("users").document(user.uid)

        // Get today's date as a String (YYYY-MM-DD) to avoid time issues
        val todayStr = getCurrentDateString()

        // Calculate yesterday's date string
        val yesterdayStr = getYesterdayDateString()

        db.runTransaction { transaction ->
            val snapshot = transaction.get(userRef)

            // Default values if user is new
            var currentStreak = 0L
            val lastCheckIn = snapshot.getString("lastCheckInDate") ?: ""

            if (snapshot.exists()) {
                currentStreak = snapshot.getLong("streak") ?: 0L
            }

            // LOGIC:
            when (lastCheckIn) {
                todayStr -> {
                    // 1. User already checked in today. Do nothing, just return current streak.
                }
                yesterdayStr -> {
                    // 2. User checked in yesterday. Increment streak!
                    currentStreak++
                }
                else -> {
                    // 3. User missed a day (or is brand new). Reset streak to 1.
                    currentStreak = 1
                }
            }

            // Write back to Firestore
            val data = hashMapOf(
                "streak" to currentStreak,
                "lastCheckInDate" to todayStr
            )
            // SetOptions.merge() ensures we don't wipe other user data (like name/age)
            transaction.set(userRef, data, SetOptions.merge())

            // Return the new streak
            currentStreak
        }.addOnSuccessListener { newStreak ->
            callback.onStreakUpdated(newStreak.toInt())
        }.addOnFailureListener { e ->
            callback.onError(e.message ?: "Unknown error")
        }
    }

    private fun getCurrentDateString(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Calendar.getInstance().time)
    }

    private fun getYesterdayDateString(): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }
}