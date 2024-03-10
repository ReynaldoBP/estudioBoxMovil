package com.massvision.estudiobox.Model

data class EncuestaDataCollectionItem(
    val intStatus:Int,
    val strMensaje:String,
    val arrayEncuesta:List<EncuestaListItem>
)
data class EncuestaListItem(
    val intIdEncuesta:Int,
    val strDescripcion:String,
    val strTitulo:String,
    val strPermiteFirma:String,
    val strPermiteDatoAdicional:String,
    val intTiempo:Int,
    val strEmpresa:String,
    val strSucursal:String,
    val strArea:String,
    val strEstado:String,
    val strusrCreacion:String,
    val strFeCreacion:String,
    val strUsrModificacion:String,
    val strFeModificacion:String
)