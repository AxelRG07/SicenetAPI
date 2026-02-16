package com.example.sicenetapi

import android.app.Application
import com.example.sicenetapi.data.AppContainer
import com.example.sicenetapi.data.DefaultAppContainer

class SicenetApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}