package com.bridge.androidtechnicaltest.ui.pupils

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object HomeDestination

fun NavGraphBuilder.pupilListScreen(
    onNavigateToPupil: (pupilId: Int?) -> Unit,
    onNavigateToMain: () -> Unit,
) {
    composable<HomeDestination> {
        PupilsListScreen(
            onNavigateToMain = onNavigateToMain,
            onNavigateToPupil = onNavigateToPupil
        )
    }
}
fun NavController.navigateToPupilsList() {
    navigate(HomeDestination){
        popUpTo(graph.id)
    }

}