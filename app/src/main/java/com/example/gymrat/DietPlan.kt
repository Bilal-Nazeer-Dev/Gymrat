package com.example.gymrat

import java.io.Serializable

data class DietPlan(
    val title: String,
    val type: String,
    val calories: String,
    val imageRes: Int,
    val description: String // Added this
) : Serializable