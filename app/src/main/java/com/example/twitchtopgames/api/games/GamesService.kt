package com.example.twitchtopgames.api.games

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.twitchtopgames.api.games.model.GameId
import com.example.twitchtopgames.api.games.model.TopIDsResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

private const val TAG = "GamesService"

class GamesService {
    private val twitchApi: GamesAPI

    init {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://api.twitch.tv/")
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        twitchApi = retrofit.create(GamesAPI::class.java)
    }

    fun getGames(clientID : String, accessesToken : String): LiveData<List<GameId>> {
        val responseLiveData: MutableLiveData<List<GameId>> = MutableLiveData()
        val twitchRequest: Call<TopIDsResponse> = twitchApi.getGames(clientID, "Bearer $accessesToken")

        twitchRequest.enqueue(object : Callback<TopIDsResponse>{
            override fun onFailure(call: Call<TopIDsResponse>, t: Throwable) {
                Log.e(TAG, "Failed to fetch photo", t)
            }
            override fun onResponse(
                call: Call<TopIDsResponse>,
                IDsResponse: Response<TopIDsResponse>
            ) {
                Log.d(TAG, "Call body ${IDsResponse}")
                Log.d(TAG, "Response received")
                val topIDsResponse: TopIDsResponse? = IDsResponse.body()
                var gameIds: List<GameId> = topIDsResponse?.mGameIds?: mutableListOf()
                responseLiveData.value = gameIds
            }
        })
        return responseLiveData
    }
}