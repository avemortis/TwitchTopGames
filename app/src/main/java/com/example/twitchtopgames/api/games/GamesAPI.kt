package com.example.twitchtopgames.api.games

import com.example.twitchtopgames.api.games.model.TopIDsResponse
import retrofit2.Call
import retrofit2.http.*

interface GamesAPI {
    @GET(
        "helix/games/top" +
                "?first=100" +
                "&api_version=5"+
                "&format=json" +
                "&nojsoncallback=1" +
                "&extras=data"
    )
    fun getGames(
        @Header("Client-Id") clientID: String,
        @Header("Authorization") AccessesToken: String
    ): Call<TopIDsResponse>
}