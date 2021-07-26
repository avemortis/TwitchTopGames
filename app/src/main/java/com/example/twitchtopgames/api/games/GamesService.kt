package com.example.twitchtopgames.api.games

import android.util.Log
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.loader.content.AsyncTaskLoader
import com.example.twitchtopgames.api.games.model.*
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
    fun getGames(clientID : String, accessesToken : String, cursor: String = String()): LiveData<TopIDsResponse> {
        val responseLiveData: MutableLiveData<TopIDsResponse> = MutableLiveData()
        val twitchRequest: Call<TopIDsResponse> = twitchApi.getNextPage(clientID, "Bearer $accessesToken", cursor)
        var topIDsResponse: TopIDsResponse?

        twitchRequest.enqueue(object : Callback<TopIDsResponse>{
            override fun onFailure(call: Call<TopIDsResponse>, t: Throwable) {
                Log.e(TAG, "Failed", t)
            }
            override fun onResponse(
                call: Call<TopIDsResponse>,
                IDsResponse: Response<TopIDsResponse>
            ) {
                Log.d(TAG, "Call body $IDsResponse")
                Log.d(TAG, "Response received")
                topIDsResponse= IDsResponse.body()
                responseLiveData.value = topIDsResponse
            }
        })

        return responseLiveData
    }

    fun getStreams(clientID: String, accessesToken: String, id: Int, cursor: String = String()) : LiveData<Streams>{
        val streamsLiveData: MutableLiveData<Streams> = MutableLiveData()
        val streamsRequest: Call<Streams> = twitchApi.getStreams(clientID, accessesToken, cursor, 100, id)
        var streamResponse : Streams?

        streamsRequest.enqueue(object : Callback<Streams>{
            override fun onResponse(call: Call<Streams>, response: Response<Streams>) {
                streamResponse = response.body()
                streamsLiveData.value = streamResponse
            }

            override fun onFailure(call: Call<Streams>, t: Throwable) {
                t.stackTrace
            }

        })
        return streamsLiveData
    }

    @WorkerThread
    fun loadStreams(
        clientID: String,
        accessesToken: String,
        id: Int,
        cursor: String = String()
    ): Streams? {
        val response = twitchApi.getStreams(clientID, accessesToken, cursor, 100, id).execute()
        return response.body()
    }
}