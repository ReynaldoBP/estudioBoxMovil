package com.massvision.estudiobox.Model

import com.google.gson.JsonObject

data class LoginDataCollectionItem(
    val intStatus:Int,
    val strMensaje:String,
    val arrayCliente:LoginListItem
)
data class LoginListItem(
    val intIdCliente:Int,
    val strIdentificacion:String,
    val strNombre:String,
    val strCorreo:String,
    val strAutenticacionRS:String,
    val strEdad:String,
    val strGenero:String,
    val strEstado:String,
    val strusrCreacion:String,
    val strFeCreacion:String,
    val strUsrModificacion:String,
    val strFeModificacion:String
)
