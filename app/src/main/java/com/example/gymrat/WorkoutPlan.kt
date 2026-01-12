package com.example.gymrat

import java.io.Serializable

data class WorkoutPlan(
    val title: String,
    val difficulty: String,
    val duration: String,
    val imageRes: Int,
    val description: String // Added this
) : Serializable