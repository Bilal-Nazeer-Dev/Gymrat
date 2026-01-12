plugins {
    id("org.jetbrains.kotlin.android")
    id("com.android.application")
    id("com.google.gms.google-services")



}

android {
    namespace = "com.example.gymrat" // Ensure this matches your package name
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.gymrat"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    // Enable ViewBinding here
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    implementation("androidx.viewpager2:viewpager2:1.0.0")
    implementation("com.google.android.material:material:1.11.0")

    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))

    implementation("com.github.bumptech.glide:glide:4.16.0")

    implementation("de.hdodenhof:circleimageview:3.1.0")


    implementation("com.google.ai.client.generativeai:generativeai:0.9.0")

    // Coroutines (Required for background tasks)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")


    // Coroutines (for background tasks)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")

    implementation("com.google.ai.client.generativeai:generativeai:0.9.0")

    // Add the dependencies for Firebase products
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")      // For Login/Signup
    implementation("com.google.firebase:firebase-firestore")

    implementation("com.google.firebase:firebase-storage-ktx:20.3.0")
    implementation("io.coil-kt:coil:2.4.0")


    // Navigation Component (We will use this for future steps)
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")

    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation("com.google.firebase:firebase-firestore-ktx:24.10.0")

    implementation("com.google.ai.client.generativeai:generativeai:0.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")

    implementation("com.airbnb.android:lottie:6.1.0")
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}