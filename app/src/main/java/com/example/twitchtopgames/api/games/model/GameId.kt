package com.example.twitchtopgames.api.games.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
@Entity
class GameId (
    @PrimaryKey var pos: Int = 0,
    @SerializedName("id") var id: Int = 0,
    @SerializedName("name") var name: String = "",
    @SerializedName("box_art_url") var posterUrl: String = "",
){
    fun setRes(weight : Int, height : Int){
        posterUrl = posterUrl.replace("{width}", weight.toString())
        posterUrl = posterUrl.replace("{height}", height.toString())
    }
}