package com.mel.medicalapp.presentation.navigation

sealed class Screen(val route: String) {
    data object WelcomeAuth : Screen("welcome_auth")
    data object Onboarding : Screen("onboarding")
    data object Home : Screen("home")
    data object AccountInfo : Screen("account_info")
    data object Scribe : Screen("scribe")
    data object Summary : Screen("summary")
    data object Questions : Screen("questions")
    data object TimelineHistory : Screen("timeline_history")
    data object Learn : Screen("learn")
    data object ResourcesRights : Screen("resources_rights")
    data object CaptureUpload : Screen("capture_upload")
    data object ProviderDashboard : Screen("provider_dashboard")
}
