package com.massvision.estudiobox.Repository
import com.google.gson.JsonObject
import com.massvision.estudiobox.Model.EmpresaDataCollectionItem
import com.massvision.estudiobox.Model.EncuestaDataCollectionItem
import com.massvision.estudiobox.Model.PreguntaDataCollectionItem
import com.massvision.estudiobox.Model.RespuestaDataCollectionItem
import org.json.JSONObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("/apiMovil/getEmpresa")
    suspend fun getEmpresa(@Body jsonObject: JsonObject):Response<EmpresaDataCollectionItem>
    @POST("/apiMovil/getEncuesta")
    suspend fun getEncuesta(@Body jsonObject: JsonObject):Response<EncuestaDataCollectionItem>
    @POST("/apiMovil/getPregunta")
    suspend fun getPregunta(@Body jsonObject: JsonObject):Response<PreguntaDataCollectionItem>
    @POST("/apiMovil/createRespuesta")
    suspend fun createRespuesta(@Body jsonObject: JsonObject):Response<RespuestaDataCollectionItem>
}