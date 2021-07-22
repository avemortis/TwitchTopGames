package com.example.twitchtopgames.api

import com.example.twitchtopgames.api.accesses.AccessesService
import com.example.twitchtopgames.api.games.GamesService

object TwitchServices {
    val accessesService = AccessesService()

    val gamesService = GamesService()
}