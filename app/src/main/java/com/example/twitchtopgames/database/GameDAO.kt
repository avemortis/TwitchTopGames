package com.example.twitchtopgames.database

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.twitchtopgames.api.games.model.GameId
import com.example.twitchtopgames.api.games.model.Stats
private const val TAG ="DAO"
@Dao
interface GameDAO {
    @Query("SELECT * FROM GameId")
    fun getGamesId(): LiveData<List<GameId>>

    @Query("SELECT * FROM Stats")
    fun getGamesStats(): LiveData<List<Stats>>

    @Insert
    fun saveGames(game: GameId)

    @Insert
    fun saveStats(stats : Stats)

    @Delete
    fun deleteGame(game: GameId)

    @Delete
    fun deleteStat(stat: Stats)

    @Delete
    fun deleteGames(gamesId: List<GameId>)

    @Delete
    fun deleteStats(gamesStat: List<Stats>)
}