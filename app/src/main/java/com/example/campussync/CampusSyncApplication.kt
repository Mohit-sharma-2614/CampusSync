package com.example.campussync

import android.app.Application
import com.example.campussync.data.AppContainer
import com.example.campussync.data.AppDataContainer

class CampusSyncApplication: Application() {
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}