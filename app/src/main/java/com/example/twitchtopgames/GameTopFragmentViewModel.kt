package com.example.twitchtopgames

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.twitchtopgames.api.games.model.GameId
import com.example.twitchtopgames.api.games.model.Stats

private const val TAG = "View Model"

class GameTopFragmentViewModel : ViewModel() {
    val games : MutableList<GameId> = mutableListOf()
    val stats : MutableList<Stats> = mutableListOf()

    val repository = GameRepository.get()
    val gamesIdsLiveData = repository.getGamesId()
    val gamesStatsLiveData = repository.getGamesStats()

    var lastCursor: String = String()

    fun addNewPage(page: List<GameId>){
        page.forEach{ checked ->
            if (cloneCheck(checked.id)){
                checked.pos = games.size
                games.add(checked)
                stats.add(Stats(0,0))
                repository.saveGames(checked)
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