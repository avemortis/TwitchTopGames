package com.example.twitchtopgames.api.accesses

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val TAG : String = "AccessesService"

class AccessesService {
    private val accessesAPI: AccessesAPI
    init {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://id.twitch.tv/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        accessesAPI = retrofit.create(AccessesAPI::class.java)
    }

    fun getAccessesToken(clientID: String, clientSecret: String): LiveData<String> {
        var responseLiveData: MutableLiveData<String> = MutableLiveData()
        val authRequest: Call<JsonObject> = accessesAPI.getAccessesResult(clientID, clientSecret, "client_credentials")

        authRequest.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                Log.d(TAG, "Accesses received ${response.body()!!.get("access_token")}")
                responseLiveData.value = response.body()!!.get("access_token").asString
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                t.stackTrace
            }

        })
        return responseLiveData
    }

/*    fun getAccessesToken(clientID: String, clientSecret: String): LiveData<AccessesResultBody> {
        val responseLiveData: MutableLiveData<AccessesResultBody> = MutableLiveData()
        val authRequest: Call<AccessesResultBody> = accessesAPI.getAccessesResult(clientID, clientSecret, "client_credentials")

        authRequest.enqueue(object : Callback<AccessesResultBody> {
            override fun onResponse(call: Call<AccessesResultBody>, response: Response<AccessesResultBody>) {
                Log.d(TAG, "Accesses received $response")
                responseLiveData.value = response.body()
            }

            override fun onFailure(call: Call<AccessesResultBody>, t: Throwable) {
                t.stackTrace
            }

        })
        return responseLiveData
    }*/

}