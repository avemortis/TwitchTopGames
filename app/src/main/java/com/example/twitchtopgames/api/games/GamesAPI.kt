package com.example.twitchtopgames.api.games

import com.example.twitchtopgames.api.games.model.Stream
import com.example.twitchtopgames.api.games.model.Streams
import com.example.twitchtopgames.api.games.model.TopIDsResponse
import retrofit2.Call
import retrofit2.http.*

interface GamesAPI {
    @GET(
        "helix/games/top"
    )
    fun getNextPage(
        @Header("Client-Id") clientID: String,
        @Header("Authorization") AccessesToken: String,
        @Query("after") Cursor: String,
        @Query("first") pageSize: Int = 10,
        @Query("api_version") apiVersion: Int = 5,
        @Query("format") format: String = "json",
        @Query("nojsoncallback") callback: Int = 5,
        @Query("extras") extras: String = "data"
    ): Call<TopIDsResponse>

    @GET(
        "helix/streams"
    )
    fun getStreams(
        @Header("Client-Id") clientID: String,
        @Header("Authorization") AccessesToken: String,
        @Query("after") Cursor: String,
        @Query("first") SizePage: Int = 100,
        @Query("game_id") GameID: Int,
        //@Query("api_version") apiVersion: Int = 5,
        //@Query("format") format: String = "json",
        //@Query("nojsoncallback") callback: Int = 5,
        //@Query("extras") extras: String = "data"
    ): Call<Streams>

}