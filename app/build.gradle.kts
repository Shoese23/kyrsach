plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp) // Используем KSP вместо kapt
}

android {
    namespace = "com.example.kyrsach"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.kyrsach"
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    // Стандартные зависимости AndroidX
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.constraintlayout)

    // Room Database (через Version Catalog)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler) // Обратите внимание: ksp(...), а не ksp "..."

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Сторонние библиотеки (добавляем напрямую, так как их нет в libs.versions.toml)
    implementation("com.github.BlackBoxVision:material-calendar-view:v3.0.0")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    // Тесты
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}