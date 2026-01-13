# 🐀 Gym Rat - AI Powered Fitness Companion

![Language](https://img.shields.io/badge/Language-Kotlin-purple) ![Platform](https://img.shields.io/badge/Platform-Android-green) ![Backend](https://img.shields.io/badge/Backend-Firebase-orange) ![AI](https://img.shields.io/badge/AI-Google%20Gemini-blue)

**Gym Rat** is a modern, native Android application designed to be your all-in-one digital fitness coach. It combines traditional fitness tracking with cutting-edge **Generative AI** to provide personalized advice, structured workout plans, and gamified consistency tracking.

---

## 🚀 Key Features

* **🤖 AI Personal Trainer:** Integrated with **Google Gemini API** to answer fitness and nutrition questions in real-time.
* **🔥 Streak System:** A gamified "Daily Attendance" feature that tracks consistency and awards streaks to keep users motivated.
* **🏋️ Structured Workouts:** A library of curated workout plans (HIIT, Strength, Yoga) with timers and history tracking.
* **🥗 Diet Plans:** Nutrition guides for various goals (Muscle Gain, Keto, Weight Loss).
* **⚖️ BMI Calculator:** Real-time body mass index calculation with health category visualization.
* **👤 Profile Management:**
    * Real-time stats updates (Age, Weight, Height).
    * **Local Storage** implementation for Profile Pictures (Privacy focused).
    * Secure Password Change functionality.

---

## 🛠️ Tech Stack

* **Language:** Kotlin
* **Architecture:** MVVM (Model-View-ViewModel)
* **UI:** XML / ViewBinding
* **Navigation:** Android Navigation Component (Single Activity Architecture)
* **Backend:** Firebase (Authentication, Firestore)
* **AI Model:** Google Gemini (Generative AI Client SDK)
* **Image Loading:** Glide

---

## ⚙️ Setup & Installation

1.  **Clone the Repository:**
    ```bash
    git clone [https://github.com/YourUsername/GymRat.git](https://github.com/YourUsername/GymRat.git)
    ```
2.  **Open in Android Studio:**
    Open the project folder in the latest version of Android Studio.

3.  **Firebase Configuration:**
    * Create a project on [Firebase Console](https://console.firebase.google.com/).
    * Add an Android App with package name: `com.example.gymrat`.
    * Download `google-services.json` and paste it into the `app/` directory.

4.  **API Key:**
    * Get an API Key from [Google AI Studio](https://aistudio.google.com/).
    * Open `ChatFragment.kt` and replace `val apiKey` with your key.

5.  **Build & Run:**
    Sync Gradle and run on an Emulator or Physical Device.

---

## 📄 License
This project is created for academic/portfolio purposes.
**Developed by Bilal Nazeer**
