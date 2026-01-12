package com.example.gymrat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.gymrat.databinding.FragmentSetupProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class SetupProfileFragment : Fragment() {

    private var _binding: FragmentSetupProfileBinding? = null
    private val binding get() = _binding!!

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSetupProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnContinue.setOnClickListener { saveDataAndContinue() }
    }

    private fun saveDataAndContinue() {
        val age = binding.etAge.text.toString().trim()
        val weight = binding.etWeight.text.toString().trim()
        val height = binding.etHeight.text.toString().trim()

        if (age.isEmpty() || weight.isEmpty() || height.isEmpty()) {
            Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        binding.btnContinue.visibility = View.INVISIBLE
        binding.progressBar.visibility = View.VISIBLE

        val userId = auth.currentUser?.uid
        if (userId != null) {
            val userStats = hashMapOf(
                "age" to age,
                "weight" to weight,
                "height" to height,
                "profile_setup_complete" to true
            )

            db.collection("users").document(userId)
                .set(userStats, SetOptions.merge())
                .addOnSuccessListener {
                    findNavController().navigate(R.id.action_setupProfileFragment_to_homeFragment)
                }
                .addOnFailureListener { e ->
                    binding.btnContinue.visibility = View.VISIBLE
                    binding.progressBar.visibility = View.INVISIBLE
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}