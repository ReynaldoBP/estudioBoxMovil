package com.massvision.estudiobox.Model

data class EmpresaDataCollectionItem(
    val intStatus:Int,
    val strMensaje:String,
    val arrayEmpresa:List<EmpresaListItem>
)
data class EmpresaListItem(
    val intIdEmpresa:Int,
    val strTipoIdentificacion:String,
    val strIdentificacion:String,
    val strRepresentanteLegal:String,
    val strRazonSocial:String,
    val strNombreComercial:String,
    val strDireccion:String,
    val strEstado:String,
    val strusrCreacion:String,
    val strFeCreacion:String,
    val strUsrModificacion:String,
    val strFeModificacion:String
)
