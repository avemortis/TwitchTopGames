package com.example.twitchtopgames.api.games.model

import com.google.gson.annotations.SerializedName

class Streams {
    @SerializedName("data")
    lateinit var streams: List<Stream>
    @SerializedName("pagination")
    lateinit var cursor: Cursor
}