package com.bridge.androidtechnicaltest.ui.pupils

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.bridge.androidtechnicaltest.data.models.local.PupilEntity


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

