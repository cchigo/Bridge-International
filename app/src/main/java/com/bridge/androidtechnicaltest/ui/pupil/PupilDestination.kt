package com.bridge.androidtechnicaltest.ui.pupil

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
private data class PupilDestination(
    val pupilId: Int? = null,
    val localId: Int? = null,

    )

fun NavGraphBuilder.pupilScreen(
    onNavigateToMain: () -> Unit,
    onNavigateUp: () -> Unit
) {
    composable<PupilDestination> { backStackEntry ->
        val pupilId = backStackEntry.arguments?.get("pupilId") as? Int
        PupilScreen(
            localId = pupilId,
            onNavigateToMain = onNavigateToMain,
            onNavigateUp = onNavigateUp
        )
    }
}

fun NavController.navigateToSinglePupil(pupilId: Int?) {
    navigate(PupilDestination(pupilId))
}
