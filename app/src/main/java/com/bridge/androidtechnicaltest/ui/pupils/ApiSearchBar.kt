package com.bridge.androidtechnicaltest.ui.pupils

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce


@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ApiSearchBar(
    modifier: Modifier = Modifier,
    onSearch: (String) -> Unit
) {
    var query by remember { mutableStateOf("") }

    // Debounce search input
    LaunchedEffect(query) {
        snapshotFlow { query }
            .debounce(500)
            .collectLatest { debouncedQuery ->
                onSearch(debouncedQuery.trim())
            }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 24.dp)
    ) {
        SearchBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            expanded = false,
            windowInsets = WindowInsets(0.dp),
            inputField = {
                SearchBarDefaults.InputField(
                    query = query,
                    onQueryChange = { query = it },
                    onSearch = { onSearch(query.trim()) },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = null)
                    },
                    placeholder = {
                        Text(text = "Search Pupils")
                    },
                    expanded = false,
                    onExpandedChange = {}
                )
            },
            onExpandedChange = {},
            content = {}
        )
    }
}