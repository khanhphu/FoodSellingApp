plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.foodsellingapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.foodsellingapp"
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
    buildFeatures{
        viewBinding=true;
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {


    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-firestore:25.0.0")
    implementation("com.google.firebase:firebase-auth:23.0.0")
    implementation("com.google.firebase:firebase-database:21.0.0")
    implementation("com.google.firebase:firebase-storage:21.0.0")
    implementation(fileTree(mapOf(
        "dir" to "C:\\Users\\Phung\\Desktop\\tonghop\\ZaloPayLib",
        "include" to listOf("*.aar", "*.jar"),
        "exclude" to listOf("")
    )))
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation ("com.hbb20:ccp:2.5.0")
    implementation ("androidx.swiperefreshlayout:swiperefreshlayout:1.2.0-alpha01")
    //load image dataggg
    implementation ("com.squareup.picasso:picasso:2.8")
    //GSON
    implementation ("com.google.code.gson:gson:2.8.7")
    //image cycle
    implementation ("de.hdodenhof:circleimageview:3.1.0")

    //
    implementation ("androidx.cardview:cardview:1.0.0")
    implementation ("androidx.recyclerview:recyclerview:1.3.2")
    implementation ("androidx.core:core:1.12.0")


    //zalopay
    implementation("com.squareup.okhttp3:okhttp:4.6.0")
    implementation("commons-codec:commons-codec:1.14")
}