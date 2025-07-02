package com.bridge.androidtechnicaltest.ui.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.bridge.androidtechnicaltest.ui.viewmodel.GetPupilsViewmodel
import com.chichi.projectsetupapp.ui.theme.AppTheme


@Composable
fun PupilListScreen(
    onNavigateToMain: () -> Unit,
    onNavigateToPupil: (pupilId: Int?) -> Unit,
    viewModel: GetPupilsViewmodel = hiltViewModel()
) {
    val lazyPagingItems = viewModel.pagedPupils.collectAsLazyPagingItems()

    Scaffold(
        topBar = { PupilSearchBar() },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { onNavigateToPupil(null) },
                text = { Text("Add new pupil") },
                icon = { Icon(imageVector = Icons.Default.Add, contentDescription = null) }
            )
        }
    ) { paddingValues ->

        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(lazyPagingItems.itemCount) { index ->
                    val pupil = lazyPagingItems[index]
                    if (pupil != null) {
                        ListItem(
                            modifier = Modifier
                                .fillMaxWidth().padding(vertical = 20.dp, horizontal = 12.dp)
                                .clickable { onNavigateToPupil(pupil.pupilId) },
                            headlineContent = {
                                Text(pupil.name ?: "Unnamed")
                            }
                        )
                    }
                }

                // Append loading indicator
                lazyPagingItems.apply {
                    when (loadState.append) {
                        is LoadState.Loading -> {
                            item {
                                Box(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }
                        is LoadState.Error -> {
                            val error = loadState.append as LoadState.Error
                            item {
                                Text(
                                    text = "Error loading more: ${error.error.localizedMessage}",
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                        else -> Unit
                    }
                }
            }

            // Initial loading
            when (val state = lazyPagingItems.loadState.refresh) {
                is LoadState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is LoadState.Error -> {
                    Text(
                        text = "Error: ${state.error.localizedMessage}",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center).padding(16.dp)
                    )
                }

                else -> Unit
            }
        }
    }
}









//
//@Composable
//fun PupilListScreen(
//    onNavigateToMain: () -> Unit,
//    onNavigateToPupil: (pupilId: Int?) -> Unit,
//    viewModel: GetPupilsViewmodel = hiltViewModel()
//) {
//    val pupilsState by viewModel.pupilsState.collectAsState()
//
//    LaunchedEffect(Unit) {
//        viewModel.loadPupils()
//    }
//
//    Scaffold(topBar = {
//        PupilSearchBar()
//    }, floatingActionButton = {
//        ExtendedFloatingActionButton(onClick = {onNavigateToPupil(null)},
//            text = { Text("Add new pupil") },
//            icon = { Icon(imageVector = Icons.Default.Add, null) })
//    }) { paddingValues ->
//
//        when (val state = pupilsState) {
//            is BaseResponse.Success -> {
//                val pList = state.data
//                Log.d("STUDENTS_TAG", "PupilListScreen: $pList")
//                LazyColumn(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .padding(paddingValues)
//                ) {
//                    items(pList) { pupil ->
//
//                        ListItem(modifier = Modifier.clickable {
//                            onNavigateToPupil(
//                                pupil.pupilId
//                            )
//                        },
//                            headlineContent = { Text(pupil.name ?: "Unnamed") })
//                    }
//                }
//            }
//
//            is BaseResponse.Loading -> {
//                Box(
//                    modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
//                ) {
//                    CircularProgressIndicator(modifier = Modifier.padding(16.dp))
//                }
//
//            }
//
//            is BaseResponse.Empty -> {
//                Text("No pupils available", modifier = Modifier.padding(16.dp))
//            }
//
//            is BaseResponse.Error<*> -> {
//                Text("Error loading pupils", modifier = Modifier.padding(16.dp))
//            }
//        }
//    }
//}

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