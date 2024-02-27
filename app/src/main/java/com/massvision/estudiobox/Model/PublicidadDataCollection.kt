package com.massvision.estudiobox.Model

data class PublicidadDataCollection(
    val intStatus:Int,
    val strMensaje:String,
    val arrayPublicidad:PublicidadListItem
)
data class PublicidadListItem(
    val intIdPublicidad:Int,
    val strTitulo:String,
    val strEstado:String,
    val strEmpresa:String,
    val strSucursal:String,
    val strArea:String,
    val strEncuesta:String,
    val intTiempo:Int,
    val arrayArchivos: List<ArchivoListItem>
)
data class ArchivoListItem(
    val strNombreArc:String,
    val strBase64Image:String,
    val strUbicacion:String
)