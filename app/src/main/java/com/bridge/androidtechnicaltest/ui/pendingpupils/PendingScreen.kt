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
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Refresh
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bridge.androidtechnicaltest.common.ResponseAlertDialog
import com.bridge.androidtechnicaltest.data.models.local.PupilEntity
import com.bridge.androidtechnicaltest.domain.PendingStatus


@Composable
fun PendingScreen(
    onNavigateToPupil: (pupilId: Int) -> Unit,
   viewModel: PendingViewModel = hiltViewModel()
) {
    val state by viewModel.pendingState.collectAsState()
    val status by viewModel.workerStatus.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }

    LaunchedEffect(status) {
        status?.let {
            when (it) {
                PendingStatus.STARTED -> {
                    dialogMessage = "PW_MockSync started..."
                    showDialog = true
                }
                PendingStatus.SUCCESS -> {
                    dialogMessage = "PW_MockSync completed!"
                    showDialog = true
                }
                PendingStatus.FAILED -> {
                    dialogMessage = "PW_MockSync failed!"
                    showDialog = true
                }

                PendingStatus.EMPTY ->{showDialog = true}
            }
        }
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
                    ,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimary,
                )

                IconButton(onClick = {
                    viewModel.onEvent(PupilEvents.SyncPupils)
                }) {
                    Icon(
                        imageVector = Icons.Rounded.Refresh,
                        contentDescription = "Sync",
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


        if (showDialog) {
            ResponseAlertDialog(
                title = "Sync Status",
                confirmButtonText = "OK",
                onConfirm = { showDialog = false },
                onDismiss = { showDialog = false },
                custom = {
                    when (status) {
                        PendingStatus.STARTED -> {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp
                                )
                                Text("Please hold on, we are sending your pupil data to the server")
                            }
                        }
                        PendingStatus.SUCCESS -> {
                            Text("Sync completed successfully!.")
                        }
                        PendingStatus.FAILED -> {
                            Text("Sync failed. Please try again.")
                        }
                      PendingStatus.EMPTY ->{
                          Text("Empty list")
                      }

                        null -> {Text("null")}
                    }
                }
            )
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
    val isClickable = pupil.isDeleted == true

    Row(modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(10.dp))
        .background( MaterialTheme.colorScheme.primaryContainer)
        .padding(12.dp)
        .alpha(if (!isClickable) 1f else 0.2f)
        .clickable(
            enabled = !isClickable,
            onClick = { onNavigateToPupil(pupil.id) }
        )

    ) {
        Column(modifier = Modifier.weight(1f)) {

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

        IconButton(onClick = { showDialog = true }) {
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

data class PupilEventState( val pupilsLocal: List<PupilEntity> = emptyList() )

sealed interface PupilEvents {
    object SyncPupils : PupilEvents
    data class DeletePupils(val localPupilId: Int) : PupilEvents
}

