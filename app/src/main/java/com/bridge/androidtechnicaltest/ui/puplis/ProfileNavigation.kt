package com.bridge.androidtechnicaltest.ui.puplis

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import nl.jovmit.navsetup.profile.PendingScreen

@Serializable
data object PendingPupilsDestination

fun NavGraphBuilder.pendingPupilsScreen() {
  composable<PendingPupilsDestination> {
    PendingScreen()
  }
}

fun NavController.pendingPupils() {
  navigate(PendingPupilsDestination) {
    popUpTo(graph.id)
  }
}