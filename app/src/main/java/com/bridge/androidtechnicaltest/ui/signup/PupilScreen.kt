package com.bridge.androidtechnicaltest.ui.signup

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.bridge.androidtechnicaltest.R
import com.chichi.projectsetupapp.ui.theme.AppTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PupilScreen(
  pupilId: Int? = null,
  onNavigateToMain: () -> Unit,
  onNavigateUp: () -> Unit
) {
  var imageUrl by remember { mutableStateOf("") }
  var name by remember { mutableStateOf("") }
  var country by remember { mutableStateOf("") }
  var longitude by remember { mutableStateOf("") }
  var latitude by remember { mutableStateOf("") }
  Log.d("PUPIL_TAG", "PupilScreen: $pupilId")

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
        .padding(16.dp)
        .verticalScroll(rememberScrollState()),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      //  Image
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



      // Name
      OutlinedTextField(
        value = name,
        onValueChange = { name = it },
        label = { Text("Name") },
        modifier = Modifier.fillMaxWidth()
      )

      // Country
      OutlinedTextField(
        value = country,
        onValueChange = { country = it },
        label = { Text("Country") },
        modifier = Modifier.fillMaxWidth()
      )

      // Longitude
      OutlinedTextField(
        value = longitude,
        onValueChange = { longitude = it },
        label = { Text("Longitude") },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
      )

      // Latitude
      OutlinedTextField(
        value = latitude,
        onValueChange = { latitude = it },
        label = { Text("Latitude") },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
      )

      Spacer(modifier = Modifier.height(24.dp))

      if (pupilId != null) {
        Button(onClick = {  }, modifier = Modifier.fillMaxWidth()) {
          Text("Save Changes")
        }

        Button(
          onClick = { },
          modifier = Modifier.fillMaxWidth(),
          colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {
          Text("Delete Pupil")
        }
      } else {
        Button(onClick = {

        }, modifier = Modifier.fillMaxWidth()) {
          Text("Create Pupil")
        }
      }
    }
  }
}




@Preview
@Composable
private fun PreviewSignUpScreen() {
  AppTheme {
//    SignUpScreen(
//      onNavigateToMain = {},
//      onNavigateUp = {},
//      emailId = 76
//    )
  }
}