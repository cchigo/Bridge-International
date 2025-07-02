package com.bridge.androidtechnicaltest.ui.login

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bridge.androidtechnicaltest.common.BaseResponse
import com.bridge.androidtechnicaltest.ui.viewmodel.GetPupilsViewmodel
import com.chichi.projectsetupapp.ui.theme.AppTheme


@Composable
fun PupilListScreen(
    onNavigateToMain: () -> Unit,
    onNavigateToPupil: (pupilId: Int?) -> Unit,
    viewModel: GetPupilsViewmodel = hiltViewModel()
) {
    val pupilsState by viewModel.pupilsState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadPupils()
    }

    Scaffold(topBar = {
        PupilSearchBar()
    }, floatingActionButton = {
        ExtendedFloatingActionButton(onClick = {onNavigateToPupil(null)},
            text = { Text("Add new pupil") },
            icon = { Icon(imageVector = Icons.Default.Add, null) })
    }) { paddingValues ->

        when (val state = pupilsState) {
            is BaseResponse.Success -> {
                val pList = state.data
                Log.d("STUDENTS_TAG", "PupilListScreen: $pList")
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    items(pList) { pupil ->

                        ListItem(modifier = Modifier.clickable {
                            onNavigateToPupil(
                                pupil.pupilId
                            )
                        },
                            headlineContent = { Text(pupil.name ?: "Unnamed") })
                    }
                }
            }

            is BaseResponse.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                }

            }

            is BaseResponse.Empty -> {
                Text("No pupils available", modifier = Modifier.padding(16.dp))
            }

            is BaseResponse.Error<*> -> {
                Text("Error loading pupils", modifier = Modifier.padding(16.dp))
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun PupilSearchBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp)
    ) {
        SearchBar(modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
            expanded = false,
            windowInsets = WindowInsets(0.dp),
            inputField = {
                SearchBarDefaults.InputField(query = "",
                    onQueryChange = {},
                    onSearch = {},
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, null)
                    },
                    placeholder = {
                        Text(text = "Search Pupils")
                    },
                    expanded = false,
                    onExpandedChange = {})
            },
            onExpandedChange = {},
            content = {})
    }
}


@Preview
@Composable
private fun PreviewEmailsList() {
    AppTheme {
        PupilListScreen(onNavigateToPupil = {}, onNavigateToMain = {})
    }
}