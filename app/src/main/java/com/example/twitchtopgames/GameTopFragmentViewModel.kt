package com.example.twitchtopgames

import androidx.lifecycle.ViewModel
import com.example.twitchtopgames.api.games.model.GameId

class GameTopFragmentViewModel : ViewModel() {
    val games : MutableList<GameId> = mutableListOf()
    val viewers : MutableList<Int> = mutableListOf()
    val channels : MutableList<Int> = mutableListOf()
    var lastCursor: String = String()

    fun addNewPage(page: List<GameId>){
        page.forEach{ checked ->
            if (cloneCheck(checked.id)){
                games.add(checked)
                viewers.add(0)
                channels.add(0)
            }
        }
    }

    private fun cloneCheck(idToCheck: Int, pageSize: Int = 10) : Boolean{
        games.takeLast(pageSize).forEach { game ->
            if (game.id == idToCheck) return false
        }
        return true
    }
}