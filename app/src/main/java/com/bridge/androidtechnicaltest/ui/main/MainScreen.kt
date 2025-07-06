package com.bridge.androidtechnicaltest.ui.main

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.bridge.androidtechnicaltest.ui.pendingpupils.PendingPupilsDestination
import com.bridge.androidtechnicaltest.ui.pendingpupils.PendingViewModel
import com.bridge.androidtechnicaltest.ui.pendingpupils.pendingPupils
import com.bridge.androidtechnicaltest.ui.pendingpupils.pendingPupilsScreen
import com.bridge.androidtechnicaltest.ui.pupils.HomeDestination
import com.bridge.androidtechnicaltest.ui.pupils.navigateToPupilsList
import com.bridge.androidtechnicaltest.ui.pupils.pupilListScreen

@Composable
fun MainScreen(
  //getPendingCount: (count: Int) -> Unit,
  onOpenEmailDetails: () -> Unit,
  onNavigateToPupil: (localId: Int?) -> Unit,
  viewModel : PendingViewModel = hiltViewModel()
) {
  val navController = rememberNavController()



  //val count = viewModel.pendingState.collectAsState().value.pupilsLocal.size

  // Optional: report the count upward
//  LaunchedEffect(count) {
//    Log.d("PENDIG_TAG", "MainScreen: $count")
//  }



  Scaffold(
    bottomBar = {
      MainBottomBar(
        pendingCount = 0,
        hierarchy = navController.currentBackStackEntryAsState().value?.destination?.hierarchy,
        onNavigateToEmails = { navController.navigateToPupilsList() },
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
        onNavigateToPupil = onNavigateToPupil
      )

      pendingPupilsScreen(
        onNavigateToPupil = onNavigateToPupil

      )
    }
  }
}

@Composable
private fun MainBottomBar(
  pendingCount:  Int,
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
