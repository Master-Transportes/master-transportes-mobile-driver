import java.util.Properties

val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) file.inputStream().use { load(it) }
}

val mapsApiKey = localProperties.getProperty("MAPS_API_KEY")
    ?: error("MAPS_API_KEY não encontrada no local.properties")

val baseUrl = localProperties.getProperty("BASE_URL")
    ?: error("BASE_URL não encontrada no local.properties")

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)

    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.master.transportes.driver"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.master.transportes.driver"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        manifestPlaceholders["MAPS_API_KEY"] = mapsApiKey

        buildConfigField("String", "BASE_URL", "\"$baseUrl\"")
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
        buildConfig = true
        compose = true
    }
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.core)

    // Com ela, o FAB mostra o pin de localização corretamente.
    implementation(libs.androidx.compose.material.icons.extended)

    // é um pacote de ferramentas do Google Play Services. Para buscar o GPS de uma forma melhor e mais otimizada
    implementation(libs.play.services.location)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Lifecycle ViewModel MVVM
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.compose)

    // Process Owner para background ou foreground
    implementation(libs.androidx.lifecycle.process)

    // Coroutines (Hj temos corroutines como o viewModelScope.launch { }
    implementation(libs.kotlinx.coroutines)

    // Retrofit (Para consumir API.)
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)

    // Retrofit usa OkHttp (O interceptor é extremamente útil para ver as requisições no Logcat durante o desenvolvimento)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)

    // Como teremos Login, vamos precisar salvar: JWT, Refresh Token, Tema...
    implementation(libs.androidx.datastore.preferences)

    // Room (persistência local do perfil do motorista)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // hiltViewModel(). A própria documentação diz que a API foi movida para um novo artefato e novo pacote.
    implementation(libs.androidx.hilt.lifecycle.viewmodel.compose)

    // Google Maps
    implementation(libs.maps.compose)

    testImplementation(libs.junit)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)

    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}