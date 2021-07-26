package com.example.twitchtopgames

import android.util.Log

private const val TAG = "Lab"

object GamesStatLab {
    val statsList : MutableList<SingleStats> = mutableListOf()



    fun add (id: Int, viewers: Int, channels: Int){
        statsList.forEach{ item->
            if (id == item.id){
                item.viewers+=viewers
                item.channels+=channels
            }
            else statsList.add(SingleStats(id, viewers, channels))
            Log.d(TAG, "added $id $viewers $channels")
        }

    }

    class SingleStats(val id: Int, var viewers: Int, var channels: Int)
}