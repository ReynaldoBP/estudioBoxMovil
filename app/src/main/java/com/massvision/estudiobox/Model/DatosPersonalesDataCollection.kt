package com.massvision.estudiobox.Model

data class DatosPersonalesDataCollectionItem(
    val intStatus:Int,
    val strMensaje:String,
    val jsonDatosPersona:PersonaListItem,
    val strPoliticaAceptada:String,
)
data class PersonaListItem(
    val existe:String,
    val nombres:String,
    val ape_pat:String,
    val ape_mat:String,
    val fechanac:String,
    val sexo:String,
    val email:String,
    val habitacion:String
)
