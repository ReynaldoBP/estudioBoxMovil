package com.massvision.estudiobox

data class EncuestaDataCollectionItem(
    val intStatus:Int,
    val strMensaje:String,
    val arrayEncuesta:List<EncuestaListItem>
)
data class EncuestaListItem(
    val intIdEncuesta:Int,
    val strDescripcion:String,
    val strTitulo:String,
    val strEstado:String,
    val strusrCreacion:String,
    val strFeCreacion:String,
    val strUsrModificacion:String,
    val strFeModificacion:String
)