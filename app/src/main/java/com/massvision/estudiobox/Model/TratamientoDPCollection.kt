package com.massvision.estudiobox.Model

data class TratamientoDPDataCollectionItem(
    val intStatus:Int,
    val strMensaje:String,
    val arrayTratamientoDP: List<TratamientoDPListItem>
)
data class TratamientoDPListItem(
    val intIdTratamientoDatosPersonales:Int,
    val strDescripcion:String,
    val strUrl:String,
    val strEstado:String,
    val strusrCreacion:String,
    val strFeCreacion:String,
    val strUsrModificacion:String,
    val strFeModificacion:String
)