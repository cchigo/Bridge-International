package com.bridge.androidtechnicaltest.ui.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.bridge.androidtechnicaltest.ui.puplis.PendingPupilsDestination
import com.bridge.androidtechnicaltest.ui.puplis.pendingPupils
import com.bridge.androidtechnicaltest.ui.puplis.pendingPupilsScreen
import com.bridge.androidtechnicaltest.ui.pupils.HomeDestination
import com.bridge.androidtechnicaltest.ui.pupils.navigateToPupilsList
import com.bridge.androidtechnicaltest.ui.pupils.pupilListScreen

@Composable
fun MainScreen(
  onOpenEmailDetails: () -> Unit,
  onComposeNewEmail: (emailId: Int?) -> Unit
) {
  val navController = rememberNavController()
  Scaffold(
    bottomBar = {
      MainBottomBar(
        hierarchy = navController.currentBackStackEntryAsState().value?.destination?.hierarchy,
        onNavigateToEmails = {
          navController.navigateToPupilsList()
                             },
        onNavigateToProfile = { navController.pendingPupils() }
      )
    }
  ) { paddingValues ->
    NavHost(
      modifier = Modifier.padding(paddingValues),
      navController = navController,
      startDestination = HomeDestination
    ) {
      // pupilslist
      pupilListScreen(
        onNavigateToMain = onOpenEmailDetails,
        onNavigateToSignUp = onComposeNewEmail
      )

      pendingPupilsScreen()
    }
  }
}

@Composable
private fun MainBottomBar(
  hierarchy: Sequence<NavDestination>?,
  onNavigateToEmails: () -> Unit,
  onNavigateToProfile: () -> Unit
) {
  NavigationBar {
    NavigationBarItem(
      selected = hierarchy?.any { it.hasRoute(HomeDestination::class) } == true,
      icon = { Icon(imageVector = Icons.Default.Person, "pupils list") },
      onClick = onNavigateToEmails
    )
    NavigationBarItem(
      selected = hierarchy?.any { it.hasRoute(PendingPupilsDestination::class) } == true,
      icon = { Icon(imageVector = Icons.Default.Refresh, "pending pupils") },
      onClick = onNavigateToProfile
    )
  }
}

@Composable
@Preview
private fun PreviewMainScreen() {

}