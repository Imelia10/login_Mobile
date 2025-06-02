plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.rewear_app1"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.rewear_app1"
        minSdk = 29
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation("androidx.legacy:legacy-support-v4:1.0.0") // Jika kamu membutuhkan pustaka legacy
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // ViewModel dan LiveData (Android Jetpack)
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime:2.7.0")
    implementation ("androidx.viewpager2:viewpager2:1.0.0")

    // Jika kamu menggunakan ViewPager2 atau pustaka yang lebih baru, kamu bisa menambahkan:
    // implementation("androidx.viewpager2:viewpager2:1.0.0")
}
