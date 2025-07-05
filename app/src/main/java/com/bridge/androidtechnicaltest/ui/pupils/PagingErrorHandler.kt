package com.bridge.androidtechnicaltest.ui.pupils

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.bridge.androidtechnicaltest.R
import com.bridge.androidtechnicaltest.data.models.local.PupilEntity

@Composable
fun LazyPagingItems<PupilEntity>.PagingErrorHandler(context: Context) {
    LaunchedEffect(key1 = loadState) {
        val refreshState = loadState.refresh
        val appendState = loadState.append

        if (refreshState is LoadState.Error) {
            val errorMessage = refreshState.error.localizedMessage ?: context.getString(R.string.unknown_error)
            val text = context.getString(R.string.error_fetching_pupils_please_try_again, errorMessage)
            Toast.makeText(context, text, Toast.LENGTH_LONG).show()
        }

        if (appendState is LoadState.Error) {
            val errorMessage = appendState.error.localizedMessage ?: context.getString(R.string.unknown_error)
            val text = context.getString(R.string.error_loading_more_pupils_please_try_again, errorMessage)
            Toast.makeText(context, text, Toast.LENGTH_LONG).show()
        }

        if (refreshState is LoadState.Error || appendState is LoadState.Error) {
            retry()
        }
    }
}