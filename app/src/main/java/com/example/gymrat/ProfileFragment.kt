package com.example.gymrat

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.gymrat.databinding.FragmentProfileBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    // Safe binding access
    private val binding get() = _binding!!

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // Listener to detect database changes in real-time
    private var profileListener: ListenerRegistration? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Back Button
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        // 2. Edit Profile Navigation
        binding.btnEditProfile.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
        }

        // 3. Log Out Logic
        binding.btnLogout.setOnClickListener {
            auth.signOut()
            findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
        }

        // 4. Change Password Dialog
        binding.btnChangePassword.setOnClickListener {
            showChangePasswordDialog()
        }

        // 5. Load User Data (Real-time)
        loadUserProfileRealTime()
    }

    private fun showChangePasswordDialog() {
        // Inflate the custom dialog layout
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_change_password, null)

        val builder = AlertDialog.Builder(requireContext())
        builder.setView(dialogView)

        val dialog = builder.create()
        // Set transparent background for rounded corners
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Find views in the dialog layout
        val etOldPass = dialogView.findViewById<TextInputEditText>(R.id.etOldPassword)
        val etNewPass = dialogView.findViewById<TextInputEditText>(R.id.etNewPassword)
        val etConfirmPass = dialogView.findViewById<TextInputEditText>(R.id.etConfirmPassword)
        val btnUpdate = dialogView.findViewById<Button>(R.id.btnUpdatePassword)

        btnUpdate.setOnClickListener {
            val oldPass = etOldPass.text.toString().trim()
            val newPass = etNewPass.text.toString().trim()
            val confirmPass = etConfirmPass.text.toString().trim()

            // --- Validation Steps ---

            // 1. Empty Check
            if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 2. Length Check
            if (newPass.length < 6) {
                Toast.makeText(context, "New password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 3. Match Check
            if (newPass != confirmPass) {
                Toast.makeText(context, "New passwords do not match!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // --- Proceed to Update ---
            btnUpdate.isEnabled = false
            btnUpdate.text = "Updating..."

            val user = auth.currentUser
            if (user != null && user.email != null) {
                // Security Check: Re-authenticate user before changing password
                val credential = EmailAuthProvider.getCredential(user.email!!, oldPass)

                user.reauthenticate(credential).addOnCompleteListener { authTask ->
                    if (authTask.isSuccessful) {
                        // Re-auth success, now update password
                        user.updatePassword(newPass).addOnCompleteListener { updateTask ->
                            if (updateTask.isSuccessful) {
                                Toast.makeText(context, "Password Updated Successfully!", Toast.LENGTH_SHORT).show()
                                dialog.dismiss()
                            } else {
                                // Update failed
                                btnUpdate.isEnabled = true
                                btnUpdate.text = "Update"
                                Toast.makeText(context, "Update Failed: ${updateTask.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        // Re-auth failed (Wrong old password)
                        btnUpdate.isEnabled = true
                        btnUpdate.text = "Update"
                        Toast.makeText(context, "Incorrect Old Password", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        dialog.show()
    }

    private fun loadUserProfileRealTime() {
        val userId = auth.currentUser?.uid ?: return

        // Listen for changes in the database
        profileListener = db.collection("users").document(userId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("ProfileFragment", "Listen failed.", e)
                    return@addSnapshotListener
                }

                // Verify binding is still valid before updating UI
                if (_binding != null && snapshot != null && snapshot.exists()) {

                    // Update Text Fields
                    _binding?.tvName?.text = snapshot.getString("user_name") ?: "No Name"
                    _binding?.tvEmail?.text = snapshot.getString("email") ?: "No Email"
                    _binding?.tvAgeValue?.text = snapshot.get("age")?.toString() ?: "--"
                    _binding?.tvWeightValue?.text = "${snapshot.get("weight")?.toString() ?: "--"} kg"
                    _binding?.tvHeightValue?.text = "${snapshot.get("height")?.toString() ?: "--"} cm"
                    _binding?.tvStreakValue?.text = "${snapshot.getLong("streak") ?: 0} Days"

                    // Update Image (With Glide Fixes)
                    val imageUrl = snapshot.getString("profile_image_url")
                    if (!imageUrl.isNullOrEmpty() && isAdded) {
                        Glide.with(this)
                            .load(imageUrl)
                            .placeholder(R.drawable.ic_profile_placeholder)
                            .error(R.drawable.ic_profile_placeholder)
                            // Ensures we don't show a stale cached image
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(_binding!!.ivProfile)
                    }
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Stop listening to DB updates to prevent crashes/memory leaks
        profileListener?.remove()
        _binding = null
    }
}