plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "tiil.edu.cuoiki"
    compileSdk = 35

    defaultConfig {
        applicationId = "tiil.edu.cuoiki"
        minSdk = 24
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

    packagingOptions {
        resources.excludes.add("META-INF/DEPENDENCIES")
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.activity:activity:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // Thư viện cho việc gọi API
    implementation("com.openai:openai-java:0.8.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")

    // Thư viện này vẫn cần thiết để sử dụng PreferenceManager
    implementation("androidx.preference:preference-ktx:1.2.1")
}