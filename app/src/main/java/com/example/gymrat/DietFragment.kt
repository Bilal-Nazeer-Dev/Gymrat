package com.example.gymrat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gymrat.databinding.FragmentDietBinding

class DietFragment : Fragment() {

    private var _binding: FragmentDietBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDietBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // --- 1. BACK BUTTON LOGIC ---
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        // --- 2. DATA PREPARATION ---
        val dietList = listOf(
            DietPlan("Muscle Gain Bulk", "High Protein", "3000 kcal", R.drawable.diet_muscle_gain,
                "Focus on surplus calories and protein.\n\nBreakfast: 4 Eggs, Oatmeal, Banana\nLunch: Chicken Breast, Rice, Broccoli\nDinner: Steak, Sweet Potato, Asparagus\nSnack: Protein Shake, Almonds"),
            DietPlan("Lean Shred", "Low Carb", "2000 kcal", R.drawable.diet_lean_shred,
                "Cut fats and carbs to reveal muscle.\n\nBreakfast: Egg Whites, Spinach\nLunch: Tuna Salad, Olive Oil\nDinner: Grilled Fish, Green Beans\nSnack: Greek Yogurt"),
            DietPlan("Plant Based Power", "Vegan", "2400 kcal", R.drawable.diet_vegan,
                "Complete plant protein sources.\n\nBreakfast: Tofu Scramble, Toast\nLunch: Lentil Curry, Quinoa\nDinner: Chickpea Stir-fry\nSnack: Hummus, Carrots"),
            DietPlan("Keto Reset", "High Fat / Keto", "1800 kcal", R.drawable.diet_keto,
                "Enter ketosis fast.\n\nBreakfast: Bulletproof Coffee, Bacon\nLunch: Cobb Salad with Avocado\nDinner: Salmon with Butter Sauce\nSnack: Cheese, Macadamia Nuts"),
            DietPlan("Balanced Maintenance", "Mixed", "2500 kcal", R.drawable.diet_balanced,
                "Maintain healthy weight.\n\nBreakfast: Yogurt, Berries, Granola\nLunch: Turkey Sandwich, Apple\nDinner: Pasta with Meat Sauce\nSnack: Fruit Bar"),
            DietPlan("Paleo Primal", "Paleo", "2200 kcal", R.drawable.diet_paleo,
                "Eat like a caveman.\n\nBreakfast: Eggs, Bacon, Fruit\nLunch: Chicken Salad (No Dressing)\nDinner: Burger Patty (No Bun), Veggies\nSnack: Beef Jerky"),
            DietPlan("Mediterranean Life", "Balanced", "2100 kcal", R.drawable.diet_mediterranean,
                "Heart healthy fats.\n\nBreakfast: Greek Yogurt, Honey\nLunch: Greek Salad, Feta, Olives\nDinner: Grilled Fish, Couscous\nSnack: Walnuts"),
            DietPlan("Intermittent Fasting 16:8", "Timing", "1900 kcal", R.drawable.diet_fasting,
                "Eat only between 12PM - 8PM.\n\n12PM: Chicken Wrap, Fruit\n4PM: Protein Bar\n8PM: Large Salmon Dinner with Rice and Veggies"),
            DietPlan("Low Fodmap Gut Health", "Specialized", "2000 kcal", R.drawable.diet_lowfodmap,
                "Easy digestion.\n\nAvoid: Onion, Garlic, Gluten.\nEat: Rice, Potatoes, Chicken, Eggs, Lactose-free milk."),
            DietPlan("High Performance Carb", "Athlete", "3200 kcal", R.drawable.diet_highcarb,
                "Fuel for endurance.\n\nBreakfast: Large Oatmeal, Bagel\nLunch: Pasta with Chicken\nDinner: Rice Bowl with Beef\nSnack: Sports Drink, Banana")
        )

        // --- 3. RECYCLERVIEW SETUP ---
        binding.rvDiet.layoutManager = LinearLayoutManager(context)
        binding.rvDiet.adapter = DietAdapter(dietList) { selectedPlan ->

            // Navigate to Detail Screen
            val bundle = Bundle().apply {
                putSerializable("diet_data", selectedPlan)
            }
            findNavController().navigate(R.id.action_dietFragment_to_dietDetailFragment, bundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}