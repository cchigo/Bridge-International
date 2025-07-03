package com.bridge.androidtechnicaltest.ui.pupils

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object HomeDestination

fun NavGraphBuilder.pupilListScreen(
    onNavigateToSignUp: (pupilId: Int?) -> Unit,
    onNavigateToMain: () -> Unit,
) {
    composable<HomeDestination> {
        PupilListScreen(
            onNavigateToMain = onNavigateToMain,
            onNavigateToPupil = onNavigateToSignUp
        )
    }
}
fun NavController.navigateToPupilsList() {
    navigate(HomeDestination){
        popUpTo(graph.id)
    }

}