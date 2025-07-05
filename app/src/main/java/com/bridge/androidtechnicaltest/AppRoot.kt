package com.bridge.androidtechnicaltest

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.bridge.androidtechnicaltest.ui.main.MainDestination
import com.bridge.androidtechnicaltest.ui.main.mainScreen
import com.bridge.androidtechnicaltest.ui.main.navigateToMain
import com.bridge.androidtechnicaltest.ui.pupil.navigateToSinglePupil
import com.bridge.androidtechnicaltest.ui.pupil.pupilScreen

@Composable
fun AppRoot() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = MainDestination
    ) {

        pupilScreen(
            onNavigateToMain = { navController.navigateToMain() },
            onNavigateUp = { navController.navigateUp() }
        )

        mainScreen(
            onOpenEmailDetails = {},
            onComposeNewEmail = { id -> navController.navigateToSinglePupil(id)},
        )
    }

}









