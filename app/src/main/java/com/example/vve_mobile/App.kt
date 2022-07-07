package com.example.vve_mobile

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.airbnb.mvrx.Mavericks

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Mavericks.initialize(this)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
}
