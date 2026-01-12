package com.example.gymrat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController // <--- 1. IMPORT THIS
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gymrat.databinding.FragmentChatBinding // Make sure XML name matches
// Required for Gemini AI
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.launch

class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    // Safe binding access to prevent crashes
    private val binding get() = _binding!!

    // 🔴 TODO: PASTE YOUR NEW API KEY HERE
    private val apiKey = "AIzaSyA3kRc_uJbFgMvE1mqS8W_SsAStF9Ld5vQ"

    private lateinit var chatAdapter: ChatAdapter
    private val messageList = ArrayList<ChatMessage>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        // --- 2. BACK BUTTON LOGIC ADDED HERE ---
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        // Handle Send Button Click
        binding.btnSend.setOnClickListener {
            val userMessage = binding.etMessage.text.toString().trim()
            if (userMessage.isNotEmpty()) {
                sendMessageToAI(userMessage)
            }
        }

        // Initial Greeting if chat is empty
        if (messageList.isEmpty()) {
            addMessageToChat("Hello! I am 'Gym Rat', your advanced AI Coach. Ask me for detailed workout plans, diet advice, or health tips!", false)
        }
    }

    private fun setupRecyclerView() {
        chatAdapter = ChatAdapter(messageList)
        binding.rvChat.adapter = chatAdapter
        binding.rvChat.layoutManager = LinearLayoutManager(context)
    }

    private fun sendMessageToAI(question: String) {
        // 1. Show User's Message immediately
        addMessageToChat(question, true)
        binding.etMessage.setText("") // Clear input field
        binding.progressBar.visibility = View.VISIBLE // Show loading spinner

        // 2. Launch Background Task for AI
        lifecycleScope.launch {
            try {
                // Initialize the AI Model
                // NOTE: Changed "2.5" to "1.5" as 2.5 is not a standard public model name yet.
                // If you have specific access to 2.5, feel free to change it back!
                val generativeModel = GenerativeModel(
                    modelName = "gemini-1.5-flash",
                    apiKey = apiKey
                )

                // 🔥 IMPROVED PROMPT: Forces detailed, structured answers
                val systemInstruction = """
                   You are a professional AI Fitness Chatbot.
                   Response rules (very important):
                   - Keep answers concise but informative
                   - Do NOT write long paragraphs
                   - Use bullet points where possible
                   - Limit the response to 5–8 short bullet points
                   - Each bullet should be 1–2 lines only
                   - Use professional and motivating tone
                   - Stay strictly relevant to the question
                   - Focus on fitness, workouts, diet, BMI, and health
                   - Avoid unnecessary explanations or storytelling
                   Answer the user's question below in a clean, professional format.
                   
                   USER QUESTION: $question
                """.trimIndent()

                // Generate Response
                val response = generativeModel.generateContent(systemInstruction)
                val aiText = response.text ?: "I'm not sure how to answer that in detail."

                // 3. Show AI Response on UI Thread
                if (_binding != null) {
                    binding.progressBar.visibility = View.GONE
                    addMessageToChat(aiText, false)
                }

            } catch (e: Exception) {
                // Handle Errors safely
                if (_binding != null) {
                    binding.progressBar.visibility = View.GONE
                    addMessageToChat("Error: ${e.localizedMessage}. (Check API Key or Internet)", false)
                }
            }
        }
    }

    private fun addMessageToChat(message: String, isUser: Boolean) {
        messageList.add(ChatMessage(message, isUser))
        chatAdapter.notifyItemInserted(messageList.size - 1)
        // Scroll to the newest message
        binding.rvChat.scrollToPosition(messageList.size - 1)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}