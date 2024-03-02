package com.dtu.deltecdtu.application

import android.app.Application
import com.dtu.deltecdtu.BuildConfig
import com.google.firebase.database.FirebaseDatabase
import timber.log.Timber

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)


        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}