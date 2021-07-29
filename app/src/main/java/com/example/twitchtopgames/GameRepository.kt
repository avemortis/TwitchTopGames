package com.example.twitchtopgames

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.example.twitchtopgames.api.games.model.GameId
import com.example.twitchtopgames.api.games.model.Stats
import com.example.twitchtopgames.database.GameDatabase
import java.lang.IllegalStateException
import java.util.concurrent.Executors

private const val DATABASE_NAME = "game"

class GameRepository private constructor(context: Context){

    private val database : GameDatabase = Room.databaseBuilder(
        context.applicationContext,
        GameDatabase::class.java,
        DATABASE_NAME
    ).build()

    private val gameDAO = database.gameDAO()
    private val executor = Executors.newSingleThreadExecutor()

    fun getGamesId(): LiveData<List<GameId>> = gameDAO.getGamesId()

    fun getGamesStats(): LiveData<List<Stats>> = gameDAO.getGamesStats()

    fun saveGames(game : GameId){
        executor.execute{
            gameDAO.saveGames(game)
        }
    }

    fun saveStats(stats : Stats){
        executor.execute{
            gameDAO.saveStats(stats)
        }
    }

    fun deleteGame(gamesId: GameId){
        executor.execute{
            gameDAO.deleteGame(gamesId)
        }
    }

    fun deleteStat(gamesStat: Stats){
        executor.execute{
            gameDAO.deleteStat(gamesStat)
        }
    }

    fun deleteGames(gamesIds: List<GameId>){
        executor.execute{
            gameDAO.deleteGames(gamesIds)
        }
    }

    fun deleteStats(gamesStats: List<Stats>){
        executor.execute{
            gameDAO.deleteStats(gamesStats)
        }
    }

    companion object {
        private var INSTANCE: GameRepository? = null

        fun initialize(context: Context){
            if (INSTANCE == null) {
                INSTANCE = GameRepository(context)
            }
        }

        fun get(): GameRepository {
            return INSTANCE ?:
            throw IllegalStateException("GameRepository must be initialized")
        }
    }
}