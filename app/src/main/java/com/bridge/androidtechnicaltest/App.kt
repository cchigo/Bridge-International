package com.bridge.androidtechnicaltest

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.WorkManager
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject


@HiltAndroidApp
class App : Application(){

    @Inject
    lateinit var workerFactory : HiltWorkerFactory
    override fun onCreate() {
        super.onCreate()


        if (BuildConfig.DEBUG) {

            Timber.plant(Timber.DebugTree())
        }

        WorkManager.initialize(
            this, Configuration.Builder()
                .setWorkerFactory(workerFactory).build()
        )

    }

}