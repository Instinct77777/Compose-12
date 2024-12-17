plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    kotlin("plugin.serialization") version "1.8.0"  // Добавляем плагин для Kotlin Serialization
}

android {
    namespace = "com.example.compose12"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.compose12"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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

    buildFeatures {
        compose = true  // Включаем поддержку Compose
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"  // Версия компилятора для Jetpack Compose
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"  // Исключаем лишние файлы из пакета
        }
    }
}

dependencies {
    // Подключаем Kotlin Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")

    // Подключаем Gson для работы с JSON (если нужно)
    implementation("com.google.code.gson:gson:2.8.9")

    // Зависимости для работы с ViewModel и жизненным циклом в Compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.0")
    implementation("androidx.activity:activity-compose:1.7.0")

    // Зависимости для работы с Android и Jetpack Compose
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    //noinspection BomWithoutPlatform
    implementation(platform(libs.androidx.compose.bom))  // Управление зависимостями Compose
    implementation(libs.androidx.ui)  // Compose UI
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)  // Material3 для Compose
    implementation(libs.androidx.navigation.compose)  // Навигация Compose
    implementation(libs.androidx.navigation.runtime.ktx)  // Навигация KTX

    // Тестовые зависимости
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))  // Тесты для Compose
    androidTestImplementation(libs.androidx.ui.test.junit4)

    // Для отладки
    debugImplementation(libs.androidx.ui.tooling)  // Утилиты для отладки Compose
    debugImplementation(libs.androidx.ui.test.manifest)
}
