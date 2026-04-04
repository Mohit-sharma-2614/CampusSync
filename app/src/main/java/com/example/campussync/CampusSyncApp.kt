package com.example.campussync

import android.app.Application
import com.example.campussync.di.initKoin
import com.google.android.datatransport.BuildConfig
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.logger.Level


class CampusSyncApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@CampusSyncApp)
            androidLogger(
                if (BuildConfig.DEBUG)
                    Level.DEBUG
                else{
                    Level.NONE
                }
            )
        }
    }
}