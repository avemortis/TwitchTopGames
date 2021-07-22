package com.example.twitchtopgames.api.games.model

import android.util.Log
import com.google.gson.annotations.SerializedName

class GameId (
    @SerializedName("id") var id: String = "",
    @SerializedName("name") var name: String = "",
    @SerializedName("box_art_url") var posterUrl: String = ""
){
    fun setRes(weight : Int, height : Int){
        posterUrl = posterUrl.replace("{width}", weight.toString())
        posterUrl = posterUrl.replace("{height}", height.toString())
    }
}