package com.example.twitchtopgames

import android.app.Application

class TwitchTopGamesApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        GameRepository.initialize(this)
    }
}