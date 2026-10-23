plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    id("maven-publish")
}

android {
    namespace = "com.sehmi.engine.screenplay"
    compileSdk = 37

    defaultConfig {
        minSdk = 30
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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

    buildFeatures {
        compose = true
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

dependencies {
    api(platform(libs.androidx.compose.bom))
    api(libs.androidx.compose.ui.test.junit4)
    api(libs.androidx.compose.ui.test.manifest)
    api(libs.androidx.compose.ui)
    api(libs.androidx.compose.material3)

    api(libs.androidx.core.ktx)
    api(libs.kotlinx.coroutines.android)
    api(libs.uiautomator)

    api(libs.log4j.api)
    api(libs.log4j.core)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
                groupId = "com.github.rajbirsehmi"
                artifactId = "compose-screenplay-engine"
                version = "1.0.0-alpha01"
            }
        }
    }
}
