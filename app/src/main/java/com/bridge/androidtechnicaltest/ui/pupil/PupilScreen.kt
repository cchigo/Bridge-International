package com.bridge.androidtechnicaltest.ui.pupil

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.bridge.androidtechnicaltest.R
import com.bridge.androidtechnicaltest.common.BaseResponse
import com.bridge.androidtechnicaltest.common.ResponseAlertDialog
import com.bridge.androidtechnicaltest.common.Utils.generateRandomImageUrl
import com.bridge.androidtechnicaltest.common.Utils.generateRandomLocation
import com.bridge.androidtechnicaltest.data.model.pupil.local.Pupil
import com.bridge.androidtechnicaltest.ui.viewmodel.PupilViewModel
import com.chichi.projectsetupapp.ui.theme.AppTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PupilScreen(
  pupilId: Int? = null,
  onNavigateToMain: () -> Unit,
  onNavigateUp: () -> Unit,
  viewModel: PupilViewModel = hiltViewModel()
) {
  val pupilState by viewModel.pupilByIdState.collectAsState()
  val createState = viewModel.createState.collectAsState().value
  val updateState = viewModel.updateState.collectAsState().value
  val deleteState = viewModel.deleteState.collectAsState().value




  var imageUrl by remember { mutableStateOf("") }
  var name by remember { mutableStateOf("") }
  var country by remember { mutableStateOf("") }
  var longitude by remember { mutableStateOf("") }
  var latitude by remember { mutableStateOf("") }
  var requestLocation by remember { mutableStateOf(false) }

  val isFormValid =
    name.isNotBlank() && country.isNotBlank() && longitude.isNotBlank() && latitude.isNotBlank()

  Log.d("PUPIL_TAG", "PupilScreen: $pupilId")
  val context = LocalContext.current

//  RequestLocationAccess(
//    trigger = requestLocation,
//    onConsumed = { requestLocation = false },
//    onLocationRetrieved = { lat, lon ->
//      latitude = lat
//      longitude = lon
//    }
//  )

  LaunchedEffect(pupilId, pupilState) {
    pupilId?.let {
      viewModel.getPupilsLocal(it)
    }

    pupilState?.let { pupil ->
      name = pupil.name ?: ""
      imageUrl = pupil.image ?: ""
      latitude = pupil.latitude.toString()
      longitude = pupil.longitude.toString()
      country = pupil.country ?: ""
    }
  }

  ObserveCreateState(
    createState = createState,
    onSuccess = onNavigateUp,
    resetState = {
      viewModel.resetAllStates()
      onNavigateUp()
    }
  )

  ObserveUpdateState(
      updateState = updateState,
    onSuccess = onNavigateUp,
    resetState = {
      viewModel.resetAllStates()
      onNavigateUp()
    }
  )

  ObserveDeleteState (
    deleteState = deleteState,
    onSuccess = onNavigateUp,
    resetState = {
      viewModel.resetAllStates()
      onNavigateUp()
    }
  )



  Box(modifier = Modifier.fillMaxSize()) {
    Scaffold(
      topBar = {
        TopAppBar(
          title = {
            Text(
              text = if (pupilId != null && pupilId != -1) "Edit Pupil" else "Create Pupil"
            )
          },
          navigationIcon = {
            IconButton(onClick = onNavigateUp) {
              Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
          }
        )
      }
    ) { paddingValues ->

      Column(
        modifier = Modifier
          .padding(paddingValues)
          .padding(horizontal = 16.dp)
          .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        Text(
          "Please fill out the pupil details below",
          textAlign = TextAlign.Center,
          modifier = Modifier
            .padding(horizontal = 16.dp)
            .align(Alignment.CenterHorizontally)
        )

        if (imageUrl.isBlank()) {
          Image(
            painter = painterResource(id = R.drawable.baseline_add_a_photo_24),
            contentDescription = "Default Camera",
            modifier = Modifier
              .size(120.dp)
              .clip(CircleShape)
              .border(2.dp, Color.Gray, CircleShape)
              .padding(32.dp)
          )
        } else {
          AsyncImage(
            model = imageUrl,
            contentDescription = "Pupil Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
              .clip(CircleShape)
              .size(120.dp)
              .border(2.dp, Color.Gray, CircleShape)
          )
        }

        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("Name") },
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = country,
          onValueChange = { country = it },
          label = { Text("Country") },
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = longitude,
          onValueChange = { },
          label = { Text("Longitude") },
          modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {},
          enabled = true,
          readOnly = true
        )

        OutlinedTextField(
          value = latitude,
          onValueChange = { },
          label = { Text("Latitude") },
          modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {},
          enabled = true,
          readOnly = true
        )

        Button(onClick = {
          val (lat, lon) = generateRandomLocation()
          latitude = lat
          longitude = lon
        }) {
          Text("Get Location")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (pupilId != null) {
          Button(
            onClick = { pupilState?.let { viewModel.updatePupil(it) } },
            enabled = isFormValid,
            modifier = Modifier.fillMaxWidth()
          ) {
            Text("Save Changes")
          }

          Button(
            onClick = { pupilState?.let { viewModel.deletePupil(it) } },
            modifier = Modifier
              .wrapContentSize()
              .align(Alignment.End),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
          ) {
            Icon(
              imageVector = Icons.Default.Delete,
              contentDescription = "Delete",
              tint = Color.White,
              modifier = Modifier.padding(end = 8.dp)
            )
            Text("Delete Pupil", color = Color.White)
          }

        } else {
          Button(
            enabled = isFormValid,
            onClick = {
              val pupil = Pupil(
                name = name,
                country = country,
                image = generateRandomImageUrl(name),
                longitude = longitude.toDouble(),
                latitude = latitude.toDouble()
              )
              viewModel.createPupil(pupil)
            },
            modifier = Modifier.fillMaxWidth()
          ) {
            Text("Create Pupil")
          }
        }
      }
    }


    // Show loader at bottom if any state is loading
    if (
      createState is BaseResponse.Loading ||
      updateState is BaseResponse.Loading ||
      deleteState is BaseResponse.Loading
    ) {
      CircularProgressIndicator(
        modifier = Modifier
          .align(Alignment.BottomCenter)
          .padding(bottom = 64.dp)
      )
    }

  }
}


@Composable
fun ObserveUpdateState(
  updateState: BaseResponse<Pupil>,
  onSuccess: () -> Unit,
  resetState: () -> Unit
) {
  var showDialog by remember { mutableStateOf(false) }
  var dialogTitle by remember { mutableStateOf("") }
  var dialogMessage by remember { mutableStateOf("") }
  var isError by remember { mutableStateOf(false) }

  LaunchedEffect(updateState) {
    when (updateState) {
      is BaseResponse.Success -> {
        dialogTitle = "Success"
        dialogMessage = "Pupil updated successfully!"
        isError = false
        showDialog = true
      }

      is BaseResponse.Error -> {
        dialogTitle = "Error"
        dialogMessage = updateState.error.title ?: "Unable to update pupil. Please retry when network is available"
        isError = true
        showDialog = true
      }


      else -> {}
    }
  }

  if (showDialog) {
    ResponseAlertDialog(
      title = dialogTitle,
      message = dialogMessage,
      confirmButtonText = "OK",
      onDismiss = {},
      onConfirm = {
        showDialog = false
        resetState()
        if (!isError) {
          onSuccess()
        }
      }
    )
  }
}



@Composable
fun ObserveCreateState(
  createState: BaseResponse<Pupil>,
  onSuccess: () -> Unit,
  resetState: () -> Unit
) {
  var showDialog by remember { mutableStateOf(false) }
  var dialogTitle by remember { mutableStateOf("") }
  var dialogMessage by remember { mutableStateOf("") }
  var isError by remember { mutableStateOf(false) }

  LaunchedEffect(createState) {
    when (createState) {
      is BaseResponse.Success -> {
        dialogTitle = "Success"
        dialogMessage = "Pupil created successfully!"
        isError = false
        showDialog = true
      }

      is BaseResponse.Error -> {
        dialogTitle = "Error"
        dialogMessage = createState.error.title
          ?: "We noticed you are offline. The pupil will be synced once you are back online."
        isError = true
        showDialog = true
      }

      else -> Unit
    }
  }

  if (showDialog) {
    ResponseAlertDialog(
      title = dialogTitle,
      message = dialogMessage,
      confirmButtonText = "OK",
      onDismiss = {},
      onConfirm = {
        showDialog = false
        resetState()
        if (!isError) {
          onSuccess()
        }
      }
    )
  }
}


@Composable
fun ObserveDeleteState(
  deleteState: BaseResponse<Pupil>,
  onSuccess: () -> Unit,
  resetState: () -> Unit
) {
  var showDialog by remember { mutableStateOf(false) }
  var dialogTitle by remember { mutableStateOf("") }
  var dialogMessage by remember { mutableStateOf("") }
  var isError by remember { mutableStateOf(false) }

  LaunchedEffect(deleteState) {
    when (deleteState) {
      is BaseResponse.Success -> {
        dialogTitle = "Success"
        dialogMessage = "Pupil deleted successfully!"
        isError = false
        showDialog = true
      }

      is BaseResponse.Error -> {
        dialogTitle = "Error"
        dialogMessage = deleteState.error.title ?: "Unable to delete pupil. Please retry when network is available."
        isError = true
        showDialog = true
      }

      else -> {}
    }
  }

  if (showDialog) {
    ResponseAlertDialog(
      title = dialogTitle,
      message = dialogMessage,
      confirmButtonText = "OK",
      confirmButtonColor = if (isError) Color.Red else MaterialTheme.colorScheme.primary,
      onDismiss = {},
      onConfirm = {
        showDialog = false
        resetState()
        if (!isError) {
          onSuccess()
        }
      }
    )
  }
}



@Preview
@Composable
private fun PreviewSignUpScreen() {
  AppTheme {
    PupilScreen(
        pupilId = 5,
        onNavigateToMain = {},
        onNavigateUp = {}
    )
  }
}