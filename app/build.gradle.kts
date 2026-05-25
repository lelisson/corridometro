import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.devtools.ksp")
}

val googleServicesFile = file("google-services.json")
val hasGoogleServicesFile = googleServicesFile.exists()

if (hasGoogleServicesFile) {
    apply(plugin = "com.google.gms.google-services")
}

val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties()
if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(keystorePropertiesFile.inputStream())
}

fun Properties.releaseProp(name: String): String =
    entries.firstOrNull { (key, _) ->
        key.toString()
            .trim()
            .removePrefix("\uFEFF")
            .removePrefix("ï»¿") == name
    }?.value?.toString()?.trim() ?: error("Falta '$name' em keystore.properties")

android {
    namespace = "com.corridometro"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.corridometro"
        minSdk = 26
        targetSdk = 35
        versionCode = 11
        versionName = "2.2.2"
        buildConfigField("boolean", "HAS_GOOGLE_SERVICES_FILE", hasGoogleServicesFile.toString())
        // Celular (ARM) + emulador (x86_64) — sem isso o Run no emulador gera APK só x86 e o Moto G31 recusa instalar
        ndk {
            abiFilters += listOf("armeabi-v7a", "arm64-v8a", "x86_64")
        }
    }

    flavorDimensions += listOf("edition", "design")
    productFlavors {
        create("standard") {
            dimension = "edition"
            buildConfigField("boolean", "REQUIRE_GOOGLE_LOGIN", "false")
        }
        create("login") {
            dimension = "edition"
            applicationIdSuffix = ".login"
            versionCode = 6
            versionName = "1.4.1-com-login"
            buildConfigField("boolean", "REQUIRE_GOOGLE_LOGIN", "true")
        }
        create("conceptA") {
            dimension = "design"
            buildConfigField("String", "MOCKUP_VARIANT", "\"INICIO\"")
            buildConfigField("String", "DESIGN_LABEL", "\"A\"")
        }
        create("conceptB") {
            dimension = "design"
            applicationIdSuffix = ".design.b"
            versionNameSuffix = "-mockup-jornada"
            buildConfigField("String", "MOCKUP_VARIANT", "\"JORNADA\"")
            buildConfigField("String", "DESIGN_LABEL", "\"B\"")
        }
    }

    signingConfigs {
        if (keystorePropertiesFile.exists()) {
            create("release") {
                val storePath = keystoreProperties.releaseProp("storeFile")
                storeFile = rootProject.file(storePath)
                storePassword = keystoreProperties.releaseProp("storePassword")
                keyAlias = keystoreProperties.releaseProp("keyAlias")
                keyPassword = keystoreProperties.releaseProp("keyPassword")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            if (keystorePropertiesFile.exists()) {
                signingConfig = signingConfigs.getByName("release")
            }
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
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2024.10.01")

    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
    implementation("androidx.navigation:navigation-compose:2.8.4")

    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")

    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.android.gms:play-services-auth:21.3.0")
    implementation("com.android.billingclient:billing-ktx:7.1.1")
    implementation("com.google.android.gms:play-services-ads:23.6.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.9.0")

    debugImplementation("androidx.compose.ui:ui-tooling")
}

gradle.projectsEvaluated {
    if (!hasGoogleServicesFile) {
        logger.warn(
            """
            |
            | [Corridometro] google-services.json AUSENTE em app/
            |   - Variante login: login Google NAO funcionara ate configurar Firebase.
            |   - Siga: FIREBASE_SETUP_LOGIN.txt ou rode: .\check-firebase-setup.ps1
            |
            """.trimMargin(),
        )
    }
}
