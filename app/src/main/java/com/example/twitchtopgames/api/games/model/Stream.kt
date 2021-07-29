package com.example.twitchtopgames.api.games.model

import com.google.gson.annotations.SerializedName

class Stream (
    @SerializedName("viewer_count") var viewers: Int = 0,
    @SerializedName("user_name") var name: String = ""
)