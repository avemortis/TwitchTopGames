package com.example.twitchtopgames.api.accesses
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.POST
import retrofit2.http.Query

interface AccessesAPI {
    @POST(
        "oauth2/token"
    )
    fun getAccessesResult (@Query("client_id") clientID : String,
                           @Query("client_secret") clientSecret: String,
                           @Query("grant_type") grantType: String):
            Call<JsonObject>
}