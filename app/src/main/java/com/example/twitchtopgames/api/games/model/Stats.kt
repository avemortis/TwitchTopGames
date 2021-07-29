package com.example.twitchtopgames.api.games.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
@Entity
class Stats (
    @PrimaryKey var pos: Int = 0,
    @SerializedName("viewers") var viewers: Int = 0,
    @SerializedName("channels") var channels : Int = 0
)