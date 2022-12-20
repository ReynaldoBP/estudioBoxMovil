package com.massvision.estudiobox.Repository
import com.google.gson.JsonObject
import com.massvision.estudiobox.EncuestaDataCollectionItem
import com.massvision.estudiobox.PreguntaDataCollectionItem
import org.json.JSONObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("/apiWeb/getEncuesta")
    suspend fun getEncuesta(@Body jsonObject: JSONObject):Response<EncuestaDataCollectionItem>
    @POST("/apiWeb/getPregunta")
    suspend fun getPregunta(@Body jsonObject: JsonObject):Response<PreguntaDataCollectionItem>
}