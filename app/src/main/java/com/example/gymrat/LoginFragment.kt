package com.example.gymrat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.gymrat.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Login Button Logic
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (validateInputs(email, password)) {
                loginUser(email, password)
            }
        }

        // Navigate to Sign Up
        binding.tvSignUp.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
        }
    }

    private fun validateInputs(email: String, pass: String): Boolean {
        if (email.isEmpty()) {
            binding.etEmail.error = "Email is required"
            binding.etEmail.requestFocus()
            return false
        }
        if (pass.isEmpty()) {
            binding.etPassword.error = "Password is required"
            binding.etPassword.requestFocus()
            return false
        }
        return true
    }

    private fun loginUser(email: String, pass: String) {
        binding.progressBar.visibility = View.VISIBLE
        binding.btnLogin.isEnabled = false

        auth.signInWithEmailAndPassword(email, pass)
            .addOnSuccessListener {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(context, "Welcome back!", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
            }
            .addOnFailureListener { e ->
                binding.progressBar.visibility = View.GONE
                binding.btnLogin.isEnabled = true

                // Custom Professional Messages
                val errorMessage = when (e) {
                    is FirebaseAuthInvalidUserException -> "Account not found. Please sign up."
                    is FirebaseAuthInvalidCredentialsException -> "Incorrect email or password."
                    else -> "Login failed: ${e.localizedMessage}"
                }
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}