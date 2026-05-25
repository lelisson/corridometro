package com.corridometro.data.auth

/**
 * Passos para o desenvolvedor configurar Firebase antes do login Google funcionar.
 * Passos marcados [verificavel] sao checados no APK; os demais sao no Console Firebase.
 */
data class FirebaseSetupStep(
    val id: Int,
    val title: String,
    val description: String,
    val isComplete: Boolean,
    val isVerifiedInApp: Boolean,
    val consoleUrl: String? = null,
)

fun buildFirebaseSetupSteps(
    hasGoogleServicesFile: Boolean,
    hasWebClientId: Boolean,
    firebaseInitialized: Boolean,
    applicationId: String,
): List<FirebaseSetupStep> = listOf(
    FirebaseSetupStep(
        id = 1,
        title = "Projeto no Firebase",
        description = "Crie um projeto em console.firebase.google.com (ex.: Corridometro).",
        isComplete = hasGoogleServicesFile,
        isVerifiedInApp = false,
        consoleUrl = "https://console.firebase.google.com/",
    ),
    FirebaseSetupStep(
        id = 2,
        title = "App Android registrado",
        description = "Adicione um app Android com o pacote exato: $applicationId",
        isComplete = hasGoogleServicesFile && hasWebClientId,
        isVerifiedInApp = hasGoogleServicesFile,
        consoleUrl = "https://console.firebase.google.com/project/_/overview",
    ),
    FirebaseSetupStep(
        id = 3,
        title = "google-services.json",
        description = "Baixe o JSON no Firebase e coloque em: app/google-services.json. Depois rode: .\\build-apk-flavor.ps1 login",
        isComplete = hasGoogleServicesFile,
        isVerifiedInApp = true,
    ),
    FirebaseSetupStep(
        id = 4,
        title = "OAuth (Web Client ID)",
        description = "O plugin Gradle preenche default_web_client_id. Sem o JSON, o login nao abre.",
        isComplete = hasWebClientId,
        isVerifiedInApp = true,
    ),
    FirebaseSetupStep(
        id = 5,
        title = "Login Google ativado",
        description = "Firebase Console > Authentication > Sign-in method > Google > Ativar > Salvar.",
        isComplete = hasWebClientId && firebaseInitialized,
        isVerifiedInApp = false,
        consoleUrl = "https://console.firebase.google.com/project/_/authentication/providers",
    ),
    FirebaseSetupStep(
        id = 6,
        title = "Firestore (banco na nuvem)",
        description = "Crie o banco Firestore. Dados ficam em users/{uid}/work_shifts e expenses.",
        isComplete = hasWebClientId && firebaseInitialized,
        isVerifiedInApp = false,
        consoleUrl = "https://console.firebase.google.com/project/_/firestore",
    ),
    FirebaseSetupStep(
        id = 7,
        title = "SHA-1 do APK (Play / celular)",
        description = "Project Settings > Your apps > SHA certificate fingerprints. Adicione debug e release.",
        isComplete = hasGoogleServicesFile,
        isVerifiedInApp = false,
        consoleUrl = "https://console.firebase.google.com/project/_/settings/general",
    ),
)
