package com.bridge.androidtechnicaltest

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bridge.androidtechnicaltest.ui.pupils.PupilListScreen
import com.bridge.androidtechnicaltest.ui.pupil.PupilScreen
import kotlinx.serialization.Serializable

@Composable
fun AppRoot() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = HomeDestination
    ) {

        pupilListScreen(
            onNavigateToMain = { },
            onNavigateToSignUp = { id -> navController.navigateToSinglePupil(id) }
        )
        pupilScreen(
            onNavigateToMain = {  },
            onNavigateUp = { navController.navigateUp() }
        )
    }

}


@Serializable
data object HomeDestination

fun NavGraphBuilder.pupilListScreen(
    onNavigateToMain: () -> Unit,
    onNavigateToSignUp: (pupilId: Int?) -> Unit,
) {
    composable<HomeDestination> {
        PupilListScreen(
            onNavigateToMain = onNavigateToMain,
            onNavigateToPupil = onNavigateToSignUp
        )
    }
}



@Serializable
private data class PupilDestination(
    val pupilId: Int? = null
)

fun NavGraphBuilder.pupilScreen(
    onNavigateToMain: () -> Unit,
    onNavigateUp: () -> Unit
) {
    composable<PupilDestination> { backStackEntry ->
        val pupilId = backStackEntry.arguments?.get("pupilId") as? Int
        PupilScreen(
            pupilId = pupilId,
            onNavigateToMain = onNavigateToMain,
            onNavigateUp = onNavigateUp
        )
    }
}

fun NavController.navigateToSinglePupil(pupilId: Int?) {
    navigate(PupilDestination(pupilId))
}

