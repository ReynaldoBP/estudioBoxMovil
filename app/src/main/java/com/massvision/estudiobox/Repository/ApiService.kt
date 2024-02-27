package com.massvision.estudiobox.Repository
import com.google.gson.JsonObject
import com.massvision.estudiobox.Model.*
import org.json.JSONObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("/apiMovil/getLogin")
    suspend fun getLogin(@Body jsonObject: JsonObject):Response<LoginDataCollectionItem>
    @POST("/apiMovil/createCliente")
    suspend fun createCliente(@Body jsonObject: JsonObject):Response<RespuestaDataCollectionItem>
    @POST("/apiMovil/getEmpresa")
    suspend fun getEmpresa(@Body jsonObject: JsonObject):Response<EmpresaDataCollectionItem>
    @POST("/apiMovil/getEncuesta")
    suspend fun getEncuesta(@Body jsonObject: JsonObject):Response<EncuestaDataCollectionItem>
    @POST("/apiMovil/getPregunta")
    suspend fun getPregunta(@Body jsonObject: JsonObject):Response<PreguntaDataCollectionItem>
    @POST("/apiMovil/createRespuesta")
    suspend fun createRespuesta(@Body jsonObject: JsonObject):Response<RespuestaDataCollectionItem>
    @POST("/apiMovil/getPublicidad")
    suspend fun getPublicidad(@Body jsonObject: JsonObject):Response<PublicidadDataCollection>
}