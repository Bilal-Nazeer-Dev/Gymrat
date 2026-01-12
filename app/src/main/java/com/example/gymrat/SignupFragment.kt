
package com.example.gymrat

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.gymrat.databinding.FragmentSignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore

class SignupFragment : Fragment() {

    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Sign Up Button Click
        binding.btnSignUp.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val confirmPass = binding.etConfirmPassword.text.toString().trim()

            if (validateInputs(name, email, password, confirmPass)) {
                registerUser(name, email, password)
            }
        }

        // Already have an account? Go to Login
        binding.tvLogin.setOnClickListener {
            findNavController().navigate(R.id.action_signupFragment_to_loginFragment)
        }
    }

    private fun validateInputs(name: String, email: String, pass: String, confirmPass: String): Boolean {
        // 1. Check if name is empty
        if (name.isEmpty()) {
            binding.etName.error = "Name is required"
            return false
        }

        // 🔥 2. NEW LOGIC: Check if name contains only Alphabets and Spaces
        // ^ means start, [a-zA-Z ] means letters and space, + means one or more, $ means end
        if (!name.matches(Regex("^[a-zA-Z ]+$"))) {
            binding.etName.error = "Name should only contain letters (no numbers or symbols)"
            return false
        }

        if (email.isEmpty()) {
            binding.etEmail.error = "Email is required"
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.error = "Please enter a valid email"
            return false
        }
        if (pass.isEmpty()) {
            binding.etPassword.error = "Password is required"
            return false
        }
        if (pass.length < 6) {
            binding.etPassword.error = "Password must be at least 6 characters"
            return false
        }
        if (pass != confirmPass) {
            binding.etConfirmPassword.error = "Passwords do not match"
            return false
        }
        return true
    }

    private fun registerUser(name: String, email: String, pass: String) {
        binding.progressBar.visibility = View.VISIBLE
        binding.btnSignUp.isEnabled = false

        auth.createUserWithEmailAndPassword(email, pass)
            .addOnSuccessListener { task ->
                val userId = task.user?.uid ?: return@addOnSuccessListener

                // Create initial user data
                val userMap = hashMapOf(
                    "user_name" to name,
                    "email" to email,
                    "streak" to 0,
                    "age" to "--",
                    "weight" to "--",
                    "height" to "--"
                )

                // Save to Firestore
                db.collection("users").document(userId).set(userMap)
                    .addOnSuccessListener {
                        if (_binding != null) {
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(context, "Account created successfully!", Toast.LENGTH_SHORT).show()

                            // Navigate to Setup Profile
                            findNavController().navigate(R.id.action_signupFragment_to_setupProfileFragment)
                        }
                    }
            }
            .addOnFailureListener { e ->
                if (_binding != null) {
                    binding.progressBar.visibility = View.GONE
                    binding.btnSignUp.isEnabled = true

                    val errorMessage = when (e) {
                        is FirebaseAuthUserCollisionException -> "This email is already registered."
                        else -> "Registration failed: ${e.localizedMessage}"
                    }
                    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
