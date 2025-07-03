package com.bridge.androidtechnicaltest.common

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

object Utils {

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } else {
            // For devices below API 23
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            activeNetworkInfo != null && activeNetworkInfo.isConnected
        }

    }

    fun generateTimestamp(): String {
        return SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss.SSS",
            Locale.getDefault()
        ).format(Date())
    }


    /**
     * Launches a coroutine safely within the Fragment’s viewLifecycleOwner using repeatOnLifecycle.
     *
     * This helps avoid memory leaks by making sure the coroutine only runs when the Fragment is in a given lifecycle state (default is STARTED),
     * and automatically stops when the view is destroyed.
     *
     */

    fun Fragment.safeLifecycleLaunch(
        state: Lifecycle.State = Lifecycle.State.STARTED,
        task: suspend CoroutineScope.() -> Unit
    ) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(state) {
                task()
            }
        }
    }

    // this simulates image url
    fun generateRandomImageUrl(pupilName: String): String {
        val baseUrl = "https://www.photos/pupils/"
        val seed = pupilName.ifBlank { UUID.randomUUID().toString().take(8) }
        return "$baseUrl-$pupilName"
    }

    // this simulates random lat and long
    fun generateRandomLocation(): Pair<String, String> {
        val lat = (10..99).random() + (0..9999).random() / 10000.0
        val lon = (10..99).random() + (0..9999).random() / 10000.0
        return String.format("%.4f", lat) to String.format("%.4f", lon)
    }



}