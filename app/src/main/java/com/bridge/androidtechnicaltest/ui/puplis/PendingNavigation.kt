package com.bridge.androidtechnicaltest.ui.puplis

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object PendingPupilsDestination

fun NavGraphBuilder.pendingPupilsScreen(
  onNavigateToPupil: (pupilId: Int?) -> Unit,
) {

  composable<PendingPupilsDestination> {
    PendingScreen(
      onNavigateToPupil = onNavigateToPupil
    )
  }
}

fun NavController.pendingPupils() {
  navigate(PendingPupilsDestination) {
    popUpTo(graph.id)
  }
}