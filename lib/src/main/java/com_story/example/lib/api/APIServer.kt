package com_story.example.lib.api

import com.google.gson.GsonBuilder
import com_story.example.lib.api.APIConst.baseUrl
import com_story.example.lib.api.APIConst.getData
import com_story.example.lib.lib_model.StoryAPI
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface APIServer {
    companion object {
        val gson = GsonBuilder()
            .setLenient()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create()
        val api = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(APIServer::class.java)
    }

    @GET(getData)
    suspend fun getnewStory(): Response<StoryAPI>
}