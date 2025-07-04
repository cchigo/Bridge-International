package com.bridge.androidtechnicaltest.ui.pupils

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.bridge.androidtechnicaltest.common.BaseResponse
import com.bridge.androidtechnicaltest.data.model.pupil.local.Pupil
import com.bridge.androidtechnicaltest.data.model.pupil.local.PupilEntity
import com.bridge.androidtechnicaltest.ui.viewmodel.GetPupilsViewmodel
import com.chichi.projectsetupapp.ui.theme.AppTheme


@Composable
fun PupilListScreen(
    onNavigateToMain: () -> Unit,
    onNavigateToPupil: (pupilId: Int?) -> Unit,
    viewModel: GetPupilsViewmodel = hiltViewModel()
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    val lazyPagingItems = viewModel.pagedPupils.collectAsLazyPagingItems()
    val pupilByIdState = viewModel.pupilByIdState.collectAsState().value
    var searchQuery by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        lazyPagingItems.refresh()
    }


    Scaffold(
        topBar = {
            ApiSearchBar(
                onSearch = { query ->
                    val id = query.toIntOrNull()
                    keyboardController?.hide()
                    if (id != null) {
                        viewModel.loadPupilById(id)
                    } else {
                        viewModel.clearPupilById()
                    }
                }
            )

        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { onNavigateToPupil(null) },
                text = { Text("Add new pupil") },
                icon = { Icon(imageVector = Icons.Default.Add, contentDescription = null) }
            )
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            ShowPupilsList(lazyPagingItems, onNavigateToPupil, pupilByIdState)

        }
    }
}


@Composable
fun ShowPupilsList(
    lazyPagingItems: LazyPagingItems<PupilEntity>,
    onNavigateToPupil: (Int?) -> Unit,
    pupilByIdState: BaseResponse<PupilEntity>? = null
) {
    val context = LocalContext.current
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {

            if (pupilByIdState != null) {

                searchResultSection(pupilByIdState, onNavigateToPupil)

            } else {
                paginatedPupilsSection(lazyPagingItems, onNavigateToPupil, context)
            }
        }

        // LoadState.refresh (initial load)
        if (pupilByIdState == null) {
            when (val refreshState = lazyPagingItems.loadState.refresh) {
                is LoadState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is LoadState.Error -> {
                    val tt = "Error: Unable to refresh list"
                    Toast.makeText(
                        context,
                        tt,
                        Toast.LENGTH_SHORT
                    ).show()
                }

                else -> Unit
            }
        }
    }
}


fun LazyListScope.searchResultSection(
    pupilByIdState: BaseResponse<PupilEntity>,
    onNavigateToPupil: (Int?) -> Unit
) {
    when (pupilByIdState) {
        is BaseResponse.Loading -> {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }

        is BaseResponse.Success -> {
            item {
                val pupil = pupilByIdState.data
                ListItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp, horizontal = 12.dp)
                        .clickable { onNavigateToPupil(pupil.id) },
                    headlineContent = {
                        Text(pupil.name ?: "Unnamed")
                    }
                )
            }
        }

        is BaseResponse.Error -> {
            Log.d("ERROR_TAG", "searchResultSection: ${pupilByIdState.error.title}")
            item {
                Text(
                    text = pupilByIdState.error.title ?: "pupil does not exist",
                    color = Color.Red,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        }

        is BaseResponse.Empty -> {
            item {
                Text(
                    text = "No results found",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        }
    }
}


fun LazyListScope.paginatedPupilsSection(
    lazyPagingItems: LazyPagingItems<PupilEntity>,
    onNavigateToPupil: (Int?) -> Unit,
    context: Context
) {
    // Main items
    items(lazyPagingItems.itemCount) { index ->
        val pupil = lazyPagingItems[index]
        if (pupil != null) {
            PupilListItem(
                pupil = pupil,
                onClick = onNavigateToPupil
            )
        }
    }

    // Pagination footer
    when (val appendState = lazyPagingItems.loadState.append) {
        is LoadState.Loading -> {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }

        is LoadState.Error -> {

        }


        else -> Unit
    }
}

@Composable
fun PupilListItem(
    pupil: PupilEntity,
    onClick: (pupilId: Int) -> Unit
) {
    val isSynced = pupil.isSynced == true

    ListItem(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 16.dp)
            .clickable { onClick(pupil.id) }
            .alpha(if (isSynced) 1f else 0.5f),
        leadingContent = {
            AsyncImage(
                model = pupil.image,
                contentDescription = "Pupil Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .border(1.dp, Color.Gray, CircleShape)
            )
        },
        headlineContent = {
            Text(text = pupil.name ?: "Unnamed")
        },
        trailingContent = {
            if (!isSynced) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Pending Sync",
                    tint = Color.Red
                )
            }
        }
    )
}


@Preview
@Composable
private fun PreviewEmailsList() {
    AppTheme {
        PupilListScreen(onNavigateToPupil = {}, onNavigateToMain = {})
    }
}