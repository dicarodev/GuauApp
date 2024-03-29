plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.guauapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.guauapp"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("androidx.navigation:navigation-fragment:2.7.6")
    implementation("androidx.navigation:navigation-ui:2.7.6")
    implementation("com.google.android.material:material:1.11.0")




    implementation("com.google.firebase:firebase-auth:21.0.8")
    implementation("com.google.firebase:firebase-database")
    implementation("com.google.firebase:firebase-storage:20.3.0")
    implementation("com.google.android.gms:play-services-auth:20.7.0")


    //Carousel
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.coordinatorlayout:coordinatorlayout:1.2.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.16.0")



    // BoM Firebase
    implementation(platform("com.google.firebase:firebase-bom:32.7.1"))

    // Add the dependency for the Firebase Authentication library
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation("com.google.firebase:firebase-auth")

    // Also add the dependency for the Google Play services library and specify its version
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation("com.google.firebase:firebase-firestore:24.10.2")

    testImplementation("junit:junit:4.13.2")

    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}