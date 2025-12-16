plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    id("com.google.devtools.ksp")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("stringfog")
}

apply(plugin = "stringfog")
configure<com.github.megatronking.stringfog.plugin.StringFogExtension> {
    implementation = "com.github.megatronking.stringfog.xor.StringFogImpl"
    packageName = "com.github.megatronking.stringfog.app"
    kg = com.github.megatronking.stringfog.plugin.kg.RandomKeyGenerator()
    mode = com.github.megatronking.stringfog.plugin.StringFogMode.base64
}

android {
    namespace = "com.tb.music.player"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.music.mute.feel.soulsound.tube"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        setProperty("archivesBaseName", "${rootProject.name}-v${versionName}-${versionCode}")
    }

    signingConfigs {
        create("release") {
            storeFile = file("../mute_tube_release.jks")
            storePassword = "thirtyfour8023@.."
            keyAlias = "thirtyfour"
            keyPassword = "thirtyfour8023@.."
        }
    }


    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation("androidx.compose.runtime:runtime-livedata")
    implementation("androidx.lifecycle:lifecycle-process:2.10.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.10.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.10.0")
    implementation("androidx.lifecycle:lifecycle-service:2.10.0")
    implementation("androidx.datastore:datastore-preferences:1.1.7")

    // ui
    // Coil 2.x (推荐用于标准 Android 项目)
    implementation("io.coil-kt:coil-compose:2.7.0")
    implementation("io.coil-kt:coil:2.7.0") // 核心库，包含基础功能
    implementation("com.github.nanihadesuka:LazyColumnScrollbar:2.2.0")
    implementation("com.valentinilk.shimmer:compose-shimmer:1.3.3")

    implementation("androidx.hilt:hilt-navigation-compose:1.3.0")

    // material3
    implementation("androidx.compose.material3.adaptive:adaptive:1.2.0")
    implementation("androidx.compose.material3:material3:1.4.0")
    implementation("androidx.palette:palette:1.0.0")

    // media3
    implementation("androidx.media3:media3-exoplayer:1.8.0")
    implementation("androidx.media3:media3-datasource-okhttp:1.8.0")
    implementation("androidx.media3:media3-session:1.8.0")
    implementation("androidx.media3:media3-exoplayer-workmanager:1.8.0")
    implementation("androidx.media3:media3-ui:1.8.0")
    implementation("androidx.media3:media3-exoplayer-dash:1.8.0")
    //room
    implementation("androidx.room:room-runtime:2.7.2")
    implementation(libs.androidx.cardview)
    implementation(libs.androidx.appcompat)
    ksp("androidx.room:room-compiler:2.7.2")
    implementation("androidx.room:room-ktx:2.7.2")
    implementation("androidx.room:room-paging:2.7.2")
    implementation("androidx.paging:paging-runtime:3.3.5")
    // apache
    implementation("org.apache.commons:commons-lang3:3.19.0")

    // ktor
    implementation("io.ktor:ktor-client-core:2.3.10")  // 最新稳定版
    implementation("io.ktor:ktor-client-okhttp:2.3.10")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.10")
    implementation("io.ktor:ktor-client-encoding:2.3.10")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.10")

    // misc
    implementation("com.mikepenz:aboutlibraries-compose-m3:12.2.4")
    implementation("org.brotli:dec:0.1.2")

    implementation("com.github.TeamNewPipe:NewPipeExtractor:4368f2b9bb7ee5b0340a0bc25375e44fe23ab105")

    //mmkv
    implementation("com.tencent:mmkv:1.3.14")

    // stringfog
    implementation("com.github.megatronking.stringfog:xor:5.0.0")

    //firebase
    implementation(platform("com.google.firebase:firebase-bom:33.16.0"))
    implementation("com.google.firebase:firebase-crashlytics-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-config-ktx")
    implementation("com.google.firebase:firebase-messaging-ktx")
    //ad
    implementation("com.google.android.gms:play-services-ads:24.5.0")
    implementation("com.google.ads.mediation:applovin:13.3.1.1")
    implementation("com.google.ads.mediation:ironsource:8.10.0.0")
    implementation("com.google.ads.mediation:mintegral:16.9.91.0")
    implementation("com.google.ads.mediation:pangle:7.3.0.5.0")
    implementation("com.unity3d.ads:unity-ads:4.16.0"){
        exclude(group = "androidx.work", module = "work-runtime-ktx")
    }
    implementation("com.google.ads.mediation:unity:4.16.0.0")

    //facebook
    implementation("com.facebook.android:facebook-android-sdk:latest.release")

    //reyun
    implementation("com.reyun.solar.engine.oversea:solar-engine-core:1.2.9.7")
    implementation("com.android.installreferrer:installreferrer:2.2")

    //lottie
    implementation("com.airbnb.android:lottie-compose:6.0.0")

    // Admob UMP
    implementation("com.google.android.ump:user-messaging-platform:4.0.0")

    implementation("com.google.accompanist:accompanist-systemuicontroller:0.28.0")
    implementation("com.google.accompanist:accompanist-insets:0.28.0")
    implementation("com.github.lihangleo2:ShadowLayout:3.4.1")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")

}