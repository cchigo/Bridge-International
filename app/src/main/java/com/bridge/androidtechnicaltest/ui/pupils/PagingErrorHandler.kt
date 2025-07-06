package com.bridge.androidtechnicaltest.ui.pupils

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.bridge.androidtechnicaltest.R
import com.bridge.androidtechnicaltest.data.models.local.PupilEntity

@Composable
fun LazyPagingItems<PupilEntity>.PagingErrorHandler(context: Context) {
    // Remember retry attempt count across recompositions
    val retryCount = remember { mutableStateOf(0) }

    LaunchedEffect(key1 = loadState) {
        val refreshState = loadState.refresh
        val appendState = loadState.append

        if (refreshState is LoadState.Error) {
           Toast.makeText(context, context.getString(R.string.error_fetching_pupils_please_try_again), Toast.LENGTH_LONG).show()
            if (retryCount.value < 1) {
                retryCount.value++
                retry()
            }
        }

        if (appendState is LoadState.Error) {
           Toast.makeText(context, context.getString(R.string.error_loading_more_pupils_please_try_again), Toast.LENGTH_LONG).show()
            if (retryCount.value < 2) {
                retryCount.value++
                retry()
            }
        }
    }
}
