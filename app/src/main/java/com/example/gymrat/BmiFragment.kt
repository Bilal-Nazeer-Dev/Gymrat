package com.example.gymrat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.gymrat.databinding.FragmentBmiBinding // Check this matches your XML name

class BmiFragment : Fragment() {

    private var _binding: FragmentBmiBinding? = null
    private val binding get() = _binding!!

    private var isMetric = true // Track which unit is selected

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBmiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Back Button Logic
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        // 2. Unit Toggle Logic
        binding.toggleUnits.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.btnMetric -> setMetricMode()
                    R.id.btnImperial -> setImperialMode()
                }
            }
        }
        // Set default state
        binding.toggleUnits.check(R.id.btnMetric)

        // 3. Calculate Button Logic
        binding.btnCalculate.setOnClickListener {
            if (validateInputs()) {
                calculateBMI()
            }
        }
    }

    private fun setMetricMode() {
        isMetric = true
        // Show CM, Hide Feet/Inches
        binding.tilHeightCm.visibility = View.VISIBLE
        binding.tilHeightFt.visibility = View.GONE
        binding.tilHeightIn.visibility = View.GONE

        // Update Hints
        binding.tilWeight.hint = "Weight (kg)"
        binding.tilHeightCm.hint = "Height (cm)"

        // Clear inputs to avoid confusion
        clearInputs()
    }

    private fun setImperialMode() {
        isMetric = false
        // Hide CM, Show Feet/Inches
        binding.tilHeightCm.visibility = View.GONE
        binding.tilHeightFt.visibility = View.VISIBLE
        binding.tilHeightIn.visibility = View.VISIBLE

        // Update Hints
        binding.tilWeight.hint = "Weight (lbs)"

        clearInputs()
    }

    private fun validateInputs(): Boolean {
        val weight = binding.etWeight.text.toString()

        if (weight.isEmpty()) {
            binding.tilWeight.error = "Enter weight"
            return false
        } else {
            binding.tilWeight.error = null
        }

        if (isMetric) {
            if (binding.etHeightCm.text.toString().isEmpty()) {
                binding.tilHeightCm.error = "Enter height"
                return false
            }
        } else {
            if (binding.etHeightFt.text.toString().isEmpty() && binding.etHeightIn.text.toString().isEmpty()) {
                Toast.makeText(context, "Please enter height (ft/in)", Toast.LENGTH_SHORT).show()
                return false
            }
        }
        return true
    }

    private fun calculateBMI() {
        // Close keyboard
        val imm = requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
        imm.hideSoftInputFromWindow(binding.root.windowToken, 0)

        val weightStr = binding.etWeight.text.toString()
        val weight = weightStr.toDoubleOrNull() ?: 0.0

        var bmi = 0.0

        if (isMetric) {
            // METRIC: BMI = kg / (m * m)
            val heightCmStr = binding.etHeightCm.text.toString()
            val heightCm = heightCmStr.toDoubleOrNull() ?: 0.0

            if (heightCm > 0) {
                val heightM = heightCm / 100.0
                bmi = weight / (heightM * heightM)
            }
        } else {
            // IMPERIAL: BMI = (lbs / (in * in)) * 703
            val ftStr = binding.etHeightFt.text.toString()
            val inStr = binding.etHeightIn.text.toString()

            val feet = ftStr.toDoubleOrNull() ?: 0.0
            val inches = inStr.toDoubleOrNull() ?: 0.0

            val totalInches = (feet * 12) + inches

            if (totalInches > 0) {
                bmi = (weight / (totalInches * totalInches)) * 703
            }
        }

        displayResult(bmi)
    }

    private fun displayResult(bmi: Double) {
        binding.layoutResult.visibility = View.VISIBLE

        // Format to 1 decimal place
        val formattedBMI = String.format("%.1f", bmi)
        binding.tvBmiScore.text = formattedBMI

        // Update Progress Indicator (Max is usually set to 40 or 50 in XML)
        binding.progressBMI.setProgress(bmi.toInt(), true)

        // Determine Category & Color
        val (category, colorRes) = when {
            bmi < 18.5 -> "Underweight" to R.color.gym_accent // You can add specific colors if you want
            bmi < 25.0 -> "Normal Weight" to R.color.gym_primary
            bmi < 30.0 -> "Overweight" to R.color.gym_accent
            else -> "Obese" to R.color.gym_accent
        }

        binding.tvBmiCategory.text = category
        // binding.tvBmiCategory.setTextColor(resources.getColor(colorRes, null)) // Optional: Change color dynamically
    }

    private fun clearInputs() {
        binding.etWeight.text?.clear()
        binding.etHeightCm.text?.clear()
        binding.etHeightFt.text?.clear()
        binding.etHeightIn.text?.clear()
        binding.layoutResult.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}