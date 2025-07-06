package com.bridge.androidtechnicaltest.ui.pendingpupils


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.bridge.androidtechnicaltest.common.ResponseAlertDialog
import com.bridge.androidtechnicaltest.data.models.local.PupilEntity
import com.bridge.androidtechnicaltest.ui.viewmodel.PendingViewModel

@Composable
fun PendingScreen(
    onNavigateToPupil: (pupilId: Int) -> Unit,
   viewModel: PendingViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val syncState by viewModel.syncState.collectAsState()

    LaunchedEffect(Unit) {
        // viewModel.insertMockPupils()
    }


    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "Pending pupils",
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 20.dp)
                        .clickable {

                            //   viewModel.insertMockPupils()
                        },
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimary,
                )

                IconButton(onClick = {
                    viewModel.onEvent(PupilEvents.SyncPupils)
                }) {
                    Icon(
                        imageVector = Icons.Rounded.Refresh,
                        contentDescription = "Sort Notes",
                        modifier = Modifier.size(35.dp),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        },
    ) { paddingValues ->
        Box() {
            LazyColumn(
                contentPadding = paddingValues,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (state.pupilsLocal.isNotEmpty()) {

                    itemsIndexed(state.pupilsLocal,
                        key = { _, pupil -> pupil.id }) { index, pupil ->

                        PupilItem(state = state,
                            index = index,
                            pupil = pupil,
                            onEvent = viewModel::onEvent,
                            onNavigateToPupil = { onNavigateToPupil(pupil.id) })
                    }
                }
            }
            if (state.pupilsLocal.isEmpty()) {
                Box(modifier = Modifier.align(Alignment.Center)) {
                    Text("No new pupils to register. All set!")
                }
            }
        }


        if (syncState != null) {
            Dialog(onDismissRequest = {}) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        when (val state = syncState) {
                            is PendingViewModel.SyncResult.Started, is PendingViewModel.SyncResult.Progress -> {
                                CircularProgressIndicator()
                                Text(
                                    "Syncing in progress...",
                                    modifier = Modifier.padding(top = 16.dp)
                                )
                            }

                            is PendingViewModel.SyncResult.Success -> {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color.Green,
                                    modifier = Modifier.size(48.dp)
                                )
                                Text(
                                    "All pupils synced successfully!",
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(onClick = { viewModel.clearSyncState() }) {
                                    Text("Close")
                                }
                            }

                            is PendingViewModel.SyncResult.Failure -> {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = Color.Red,
                                    modifier = Modifier.size(48.dp)
                                )
                                Text(
                                    state.message ?: "Failed to sync ${state.failedCount} pupil(s)",
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(onClick = { viewModel.clearSyncState() }) {
                                    Text("Close")
                                }
                            }

                            null -> Unit
                        }

                    }
                }
            }
        }


    }

}


@Composable
fun PupilItem(
    state: PupilEventState,
    index: Int,
    pupil: PupilEntity,
    onEvent: (PupilEvents) -> Unit,
    onNavigateToPupil: (pupilId: Int) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    Row(modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(10.dp))
        .background(MaterialTheme.colorScheme.primaryContainer)
        .padding(12.dp)
        .clickable {
            onNavigateToPupil(pupil.id)
        }) {
        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = pupil.name ?: "sample",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = pupil.country ?: "USA",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )

        }

        IconButton(onClick = {
            showDialog = true

        }) {

            Icon(
                imageVector = Icons.Rounded.Delete,
                contentDescription = "Delete",
                modifier = Modifier.size(35.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )

        }

        if (showDialog) {
            ResponseAlertDialog(title = "Warning",
                message = "Are you sure you want to delete ${pupil.name}",
                confirmButtonText = "Yes",
                confirmButtonColor = Color.Red,
                onDismiss = {
                    showDialog = false
                },
                onConfirm = {
                    showDialog = false

                    onEvent(PupilEvents.DeletePupils(state.pupilsLocal[index].id))

                })
        }

    }


}

data class PupilEventState(

    val pupilsLocal: List<PupilEntity> = emptyList(),
    val name: MutableState<String> = mutableStateOf(""),
    val country: MutableState<String> = mutableStateOf(""),
    val latitude: MutableState<Double> = mutableDoubleStateOf(0.00),
    val longitude: MutableState<Double> = mutableDoubleStateOf(0.00)

)

sealed interface PupilEvents {
    object SortPupils : PupilEvents
    object SyncPupils : PupilEvents
    //object RetrySync : PupilEvents

    data class DeletePupils(val localPupilId: Int) : PupilEvents

    data class SavePupilEntity(
        val name: String,
        val country: String,
        val latitude: Double,
        val longitude: Double,
    ) : PupilEvents
}

