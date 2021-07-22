package com.example.twitchtopgames.api.games.model

import com.google.gson.annotations.SerializedName

class TopIDsResponse {
    @SerializedName("data")
    lateinit var mGameIds: List<GameId>
}