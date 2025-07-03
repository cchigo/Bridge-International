package com.bridge.androidtechnicaltest.ui.main

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object MainDestination

fun NavGraphBuilder.mainScreen(
  onOpenEmailDetails: () -> Unit,
  onComposeNewEmail: (emailId: Int?) -> Unit
) {
  composable<MainDestination> {
    MainScreen(
      onOpenEmailDetails = onOpenEmailDetails,
      onNavigateToPupil = onComposeNewEmail
    )
  }
}

fun NavController.navigateToMain() {
  navigate(MainDestination) {
    popUpTo(0) {
      inclusive = true
    }
  }
}