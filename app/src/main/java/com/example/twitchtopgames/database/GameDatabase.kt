package com.example.twitchtopgames.database


import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.twitchtopgames.api.games.model.GameId
import com.example.twitchtopgames.api.games.model.Stats

@Database(entities = [GameId::class, Stats::class], version = 1, exportSchema = false)
abstract class GameDatabase : RoomDatabase() {
    abstract fun gameDAO(): GameDAO

}