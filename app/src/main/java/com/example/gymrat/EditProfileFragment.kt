package com.example.gymrat

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.gymrat.databinding.FragmentEditProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.io.File
import java.io.FileOutputStream

class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // Note: We don't need FirebaseStorage anymore!

    private var selectedImageUri: Uri? = null

    // 1. IMAGE PICKER
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            selectedImageUri = result.data?.data
            if (selectedImageUri != null) {
                // Show preview immediately
                Glide.with(this)
                    .load(selectedImageUri)
                    .circleCrop()
                    .into(binding.ivProfileImage)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener { findNavController().navigateUp() }

        loadUserData()

        // 2. Open Gallery
        binding.tvChangePhoto.setOnClickListener { openGallery() }
        binding.ivProfileImage.setOnClickListener { openGallery() }

        // 3. Save Changes
        binding.btnSave.setOnClickListener {
            saveProfileChanges()
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }

    private fun loadUserData() {
        val userId = auth.currentUser?.uid ?: return

        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (_binding != null && document.exists()) {
                    binding.etName.setText(document.getString("user_name"))
                    binding.etAge.setText(document.get("age")?.toString()?.takeIf { it != "--" } ?: "")
                    binding.etWeight.setText(document.get("weight")?.toString()?.takeIf { it != "--" } ?: "")
                    binding.etHeight.setText(document.get("height")?.toString()?.takeIf { it != "--" } ?: "")

                    val imageUrl = document.getString("profile_image_url")
                    if (!imageUrl.isNullOrEmpty()) {
                        // Glide works automatically with local file paths too!
                        Glide.with(this)
                            .load(imageUrl)
                            .placeholder(R.drawable.ic_profile_placeholder)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(binding.ivProfileImage)
                    }
                }
            }
    }

    private fun saveProfileChanges() {
        val userId = auth.currentUser?.uid ?: return
        val name = binding.etName.text.toString().trim()
        val age = binding.etAge.text.toString().trim()
        val weight = binding.etWeight.text.toString().trim()
        val height = binding.etHeight.text.toString().trim()

        if (name.isEmpty()) {
            Toast.makeText(context, "Name cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        binding.progressBar.visibility = View.VISIBLE
        binding.btnSave.isEnabled = false
        binding.btnSave.text = "Saving..."

        // 🔥 LOGIC CHANGE: Save to Local Storage instead of Firebase Cloud
        var finalImageUrl: String? = null

        if (selectedImageUri != null) {
            // Copy the image to the app's internal storage
            finalImageUrl = saveImageToInternalStorage(selectedImageUri!!)
        }

        // If upload failed or no new image, try to keep the old one (logic handled in updateFirestore)
        updateFirestore(userId, name, age, weight, height, finalImageUrl)
    }

    // 🔥 NEW FUNCTION: COPIES IMAGE TO YOUR PHONE'S PRIVATE FOLDER
    private fun saveImageToInternalStorage(uri: Uri): String? {
        return try {
            val context = requireContext()
            // 1. Open the stream from the gallery
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null

            // 2. Create a file in your app's private storage
            val fileName = "profile_${System.currentTimeMillis()}.jpg"
            val file = File(context.filesDir, fileName)

            // 3. Copy the bytes
            val outputStream = FileOutputStream(file)
            inputStream.copyTo(outputStream)

            // 4. Close streams
            inputStream.close()
            outputStream.close()

            // 5. Return the local path (e.g., /data/user/0/.../profile_123.jpg)
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun updateFirestore(userId: String, name: String, age: String, weight: String, height: String, localPath: String?) {
        val updates = hashMapOf<String, Any>(
            "user_name" to name,
            "age" to age,
            "weight" to weight,
            "height" to height
        )

        // Only update the path if we successfully saved a new one
        if (localPath != null) {
            updates["profile_image_url"] = localPath
        }

        db.collection("users").document(userId)
            .update(updates) // Use update so we don't overwrite other fields (like streak)
            .addOnSuccessListener {
                if (_binding != null) {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(context, "Profile Saved Locally!", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
            }
            .addOnFailureListener {
                if (_binding != null) {
                    binding.progressBar.visibility = View.GONE
                    binding.btnSave.isEnabled = true
                    Toast.makeText(context, "Error saving data", Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}