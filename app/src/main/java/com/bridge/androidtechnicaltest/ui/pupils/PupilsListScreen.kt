package com.bridge.androidtechnicaltest.ui.pupils
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.bridge.androidtechnicaltest.R
import com.bridge.androidtechnicaltest.common.BaseResponse
import com.bridge.androidtechnicaltest.data.models.local.PupilEntity
import com.bridge.androidtechnicaltest.ui.viewmodel.PupilsViewmodel
import kotlinx.coroutines.delay

@Composable
fun PupilsListScreen(
    onNavigateToMain: () -> Unit,
    onNavigateToPupil: (pupilId: Int?) -> Unit,
    viewModel: PupilsViewmodel = hiltViewModel()
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val pupils = viewModel.pupilsPagingFlow.collectAsLazyPagingItems()
    val pupilByIdState = viewModel.pupilByIdState.collectAsState().value

    val shouldRetryLoad by viewModel.shouldRetryLoad.collectAsState()
    val isConnected by viewModel.isConnected.collectAsState()


    LaunchedEffect(shouldRetryLoad, isConnected) {
        if (shouldRetryLoad && !isConnected) {
           while (!isConnected) {
                delay(3000)
            }
            if (isConnected && shouldRetryLoad) {
              pupils.refresh()
            }
        }
    }
    pupils.PagingErrorHandler(context)

    Scaffold (
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
                text = { Text(stringResource(R.string.add_new_pupil)) },
                icon = { Icon(imageVector = Icons.Default.Add, contentDescription = null) }
            )
        }
    ){ paddingValues ->

        Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)) {


            if(pupils.loadState.refresh is LoadState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {

                    if (pupilByIdState != null) {
                        searchResultSection(pupilByIdState, onNavigateToPupil)
                    }else{
                        items(pupils.itemCount) { index ->
                            val pupil = pupils[index]
                            if (pupil != null) {

                                PupilListItem (
                                    pupil = pupil,
                                    onClick = onNavigateToPupil
                                )
                            }
                        }

                        item {
                            if(pupils.loadState.append is LoadState.Loading) {
                                CircularProgressIndicator()
                            }
                        }}
                    item{}
                }

            }
            if(pupils.loadState.hasError){

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .background(Color.Transparent)
                        .padding(top = 0.dp, start = 12.dp),
                    horizontalAlignment = Alignment.Start
                ) {


                    Button(
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                        onClick = { pupils.retry() }
                    ) {
                        Icon(

                            imageVector = Icons.Filled.Refresh,
                            contentDescription = "Retry",
                        )
                    }
                }

            }


        }
    }

}


fun LazyListScope.searchResultSection(
    pupilByIdState: BaseResponse<PupilEntity>,
    onNavigateToPupil: (Int?) -> Unit
) {
    val boxModifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    when (pupilByIdState) {
        is BaseResponse.Loading -> {
            item {
                Box(
                    modifier = boxModifier,
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
            item {
                Text(
                    text = pupilByIdState.error.title ?: stringResource(R.string.pupil_does_not_exist),
                    color = Color.Red,
                    modifier = boxModifier
                )
            }
        }

        is BaseResponse.Empty -> {
            item {
                Text(
                    text = stringResource(R.string.no_results_found),
                    modifier = boxModifier
                )
            }
        }
    }
}



