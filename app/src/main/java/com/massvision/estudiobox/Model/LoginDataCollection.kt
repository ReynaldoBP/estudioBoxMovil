package com.massvision.estudiobox.Model

data class LoginDataCollectionItem(
    val intStatus:Int,
    val strMensaje:String,
    val arrayEmpresa:List<LoginListItem>
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
