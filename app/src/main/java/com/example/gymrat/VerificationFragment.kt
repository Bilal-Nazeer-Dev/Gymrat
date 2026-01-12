package com.example.gymrat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.gymrat.databinding.FragmentVerificationBinding
import com.google.firebase.auth.FirebaseAuth

class VerificationFragment : Fragment() {

    private var _binding: FragmentVerificationBinding? = null
    private val binding get() = _binding!!
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVerificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Send Verification Email immediately when screen loads
        sendVerificationEmail()

        // 2. Button Listener: Check if verified
        binding.btnVerify.setOnClickListener {
            checkVerificationStatus()
        }

        // 3. Resend Listener
        binding.tvResend.setOnClickListener {
            sendVerificationEmail()
        }
    }

    private fun sendVerificationEmail() {
        val user = auth.currentUser

        binding.progressBar.visibility = View.VISIBLE
        binding.btnVerify.isEnabled = false

        user?.sendEmailVerification()
            ?.addOnSuccessListener {
                binding.progressBar.visibility = View.GONE
                binding.btnVerify.isEnabled = true
                Toast.makeText(context, "Verification email sent!", Toast.LENGTH_SHORT).show()
            }
            ?.addOnFailureListener { e ->
                binding.progressBar.visibility = View.GONE
                binding.btnVerify.isEnabled = true
                Toast.makeText(context, "Failed to send email: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun checkVerificationStatus() {
        val user = auth.currentUser

        binding.progressBar.visibility = View.VISIBLE

        // We must reload() the user to get the latest status from Firebase
        user?.reload()?.addOnSuccessListener {
            binding.progressBar.visibility = View.GONE

            if (user.isEmailVerified) {
                Toast.makeText(context, "Email Verified!", Toast.LENGTH_SHORT).show()

                // SUCCESS: Navigate to Profile Setup (NOT Home yet)
                // Use the action ID from verification -> setupProfile
                findNavController().navigate(R.id.action_verificationFragment_to_setupProfileFragment)
            } else {
                Toast.makeText(context, "Email not verified yet. Please check your inbox.", Toast.LENGTH_SHORT).show()
            }
        }?.addOnFailureListener {
            binding.progressBar.visibility = View.GONE
            Toast.makeText(context, "Error checking status.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}