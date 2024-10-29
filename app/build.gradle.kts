

plugins {
    id ("org.jetbrains.kotlin.android")
    id ("com.android.application")
    //id ("kotlin-kapt")
    id ("com.google.dagger.hilt.android")
    id ("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("kotlin-parcelize")
    id("com.google.devtools.ksp")
}



android {
    namespace = "com.hopeinyang.zeroknowledge"
    compileSdk = 34


    defaultConfig {
        applicationId = "com.hopeinyang.zeroknowledge"
        minSdk = 29
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.13"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildToolsVersion = "34.0.0"
}

dependencies {


    //implementation 'androidx.compose.material:material:1.5.4'




    // Optional - Integration with LiveData
    //implementation("androidx.compose.runtime:runtime-livedata")




    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.4")

    implementation("androidx.activity:activity-compose:1.9.1")
    implementation(platform("androidx.compose:compose-bom:2024.08.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.lifecycle:lifecycle-runtime-compose-android:2.8.4")
    // Optional - Integration with ViewModels
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.4")
    implementation ("androidx.navigation:navigation-compose:2.7.7")

    implementation ("androidx.constraintlayout:constraintlayout-compose:1.0.1")

    implementation("androidx.compose.material:material-icons-core")
    implementation("androidx.compose.material3:material3-window-size-class")

    implementation("com.google.dagger:hilt-android:2.48")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    implementation ("androidx.lifecycle:lifecycle-process:2.8.4")

    implementation("androidx.room:room-common:2.6.1")
    implementation("com.google.android.gms:play-services-location:21.3.0")



    //implementation("com.google.dagger:hilt-android-compiler:2.48")
    ksp ("com.google.dagger:hilt-android-compiler:2.48")

    // firebase dependencies
    implementation (platform("com.google.firebase:firebase-bom:33.1.2"))
    implementation("com.google.firebase:firebase-storage")
    implementation ("com.google.firebase:firebase-common")
    implementation ("com.google.firebase:firebase-firestore")
    implementation ("com.google.firebase:firebase-auth")
    implementation ("com.google.firebase:firebase-crashlytics")
    implementation("com.google.firebase:firebase-functions")
    //implementation ("androidx.browser:browser:1.8.0")

    implementation ("com.squareup.okhttp3:okhttp:4.9.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.0")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")


    implementation ("com.jakewharton.timber:timber:4.7.1")
    implementation("androidx.compose.ui:ui-text-google-fonts:1.6.8")

    // SMS Retrieval API Dependencies for Auto OTP Verification
    implementation("com.google.android.gms:play-services-auth-api-phone:18.1.0")
    implementation("com.google.android.gms:play-services-auth:21.2.0")

    // Room Database dependencies

    implementation("androidx.room:room-runtime:2.6.1")
    ksp ("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")

    // image loading library backed kotlin coroutine
    //implementation("io.coil-kt:coil:2.7.0")
    implementation("io.coil-kt:coil-compose:2.7.0")

    // Preferences Datastore dependencies
    implementation ("androidx.datastore:datastore-preferences:1.1.1")

    // Biometric dependencies
    implementation ("androidx.biometric:biometric:1.1.0")

    // Helper library to handle permissions
    implementation ("com.karumi:dexter:6.2.3")

    implementation ("com.google.accompanist:accompanist-permissions:0.34.0")


    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.06.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}


//kapt{
//    correctErrorTypes = true
//}