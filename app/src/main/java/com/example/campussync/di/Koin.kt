package com.example.campussync.di

import org.koin.core.context.GlobalContext.startKoin
import org.koin.dsl.KoinAppDeclaration


// Init koin function to initialize koin
fun initKoin(appDeclaration: KoinAppDeclaration? = null) {
    startKoin {
        appDeclaration?.invoke(this)
        modules(
            networkModule,
            appModule,
            apiModule,
            repoModule,
            managerModule,
            connectivityObserverModule
        )
    }
}