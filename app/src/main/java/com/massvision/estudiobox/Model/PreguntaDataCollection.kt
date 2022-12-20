package com.massvision.estudiobox

data class PreguntaDataCollectionItem(
    val intStatus:Int,
    val strMensaje:String,
    val arrayPregunta:List<PreguntaListItem>
)
data class PreguntaListItem(
    val intIdPregunta:Int,
    val strPregunta:String,
    val strEsObligatoria:String,
    val strEncuesta:String,
    val strTipoOpcionRespuesta:String,
    val intCantidadEstrellas:Int,
    val strEstado:String,
    val strusrCreacion:String,
    val strFeCreacion:String,
    val strUsrModificacion:String,
    val strFeModificacion:String
)