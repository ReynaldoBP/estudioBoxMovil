package com.massvision.estudiobox.View

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.lifecycle.lifecycleScope
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.gson.JsonObject
import com.massvision.estudiobox.R
import com.massvision.estudiobox.Repository.ApiService
import com.massvision.estudiobox.Repository.RetrofitHelper
import kotlinx.android.synthetic.main.activity_datos_personales.*
private var arrayTipoDocumento = ArrayList<String>()

class DatosPersonalesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_datos_personales)
        val bundle= intent.extras
        val idEncuesta = bundle?.getInt("idEncuesta")
        val titulo = bundle?.getString("titulo")
        val descripcion = bundle?.getString("descripcion")
        val permiteFirma = bundle?.getString("permiteFirma")
        val permiteDatoAdicional = bundle?.getString("permiteDatoAdicional")
        val tiempoDeEspera = bundle?.getInt("tiempoDeEspera")
        val idEmpresa = bundle?.getInt("idEmpresa")
        val email = bundle?.getString("email")
        if (idEncuesta != null && titulo!= null && descripcion!= null && permiteFirma!= null && permiteDatoAdicional!= null && tiempoDeEspera!= null && email!= null && idEmpresa!= null) {
            val jsonParametrosTratamiento = JsonObject()
            jsonParametrosTratamiento.addProperty("idEncuesta", idEncuesta)
            jsonParametrosTratamiento.addProperty("titulo", titulo)
            jsonParametrosTratamiento.addProperty("descripcion", descripcion)
            jsonParametrosTratamiento.addProperty("permiteFirma", permiteFirma)
            jsonParametrosTratamiento.addProperty("permiteDatoAdicional", permiteDatoAdicional)
            jsonParametrosTratamiento.addProperty("tiempoDeEspera", tiempoDeEspera)
            jsonParametrosTratamiento.addProperty("email", email)
            jsonParametrosTratamiento.addProperty("idEmpresa", idEmpresa)
            Log.d("Interceptor",jsonParametrosTratamiento.toString())
            setup(jsonParametrosTratamiento)
        }
    }
    private fun setup(jsonParametros: JsonObject)
    {
        title = "Datos Personales"
        val jsonData = JsonObject()
        val generalLayout = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        // Llenamos los datos del Tipo de Identificación
        arrayTipoDocumento.add("Seleccione su Tipo de Documento")
        val spinnerTipoDocumento = findViewById<Spinner>(R.id.tipoDocumentoSpinner)
        spinnerTipoDocumento.setGravity(Gravity.CENTER_VERTICAL or Gravity.CENTER_HORIZONTAL)
        spinnerTipoDocumento.setLayoutParams(generalLayout)
        // Llenamos los datos del País
        val arrayPais = ArrayList<String>()
        arrayPais.add("Ecuador")
        arrayPais.add("Otros")
        val spinnerPais = findViewById<Spinner>(R.id.paisSpinner)
        val spinnerArrayPaisAdapter = ArrayAdapter(
            this@DatosPersonalesActivity,
            android.R.layout.simple_spinner_dropdown_item,
            arrayPais
        )
        spinnerPais.adapter = spinnerArrayPaisAdapter
        spinnerPais.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            //((TextView) adapterView.getChildAt(0)).setGravity(Gravity.CENTER)
            override fun onNothingSelected(parent: AdapterView<*>?) {
                Toast.makeText(
                    this@DatosPersonalesActivity,
                    "onNothingSelected",
                    Toast.LENGTH_LONG,
                ).show()
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                Log.d("Interceptor",arrayPais[position].toString())
                if(arrayPais[position].toString() == "Ecuador")
                {
                    arrayTipoDocumento = ArrayList<String>()
                    arrayTipoDocumento.add("CED-CÉDULA DE CIUDADANÍA")
                }
                else
                {
                    arrayTipoDocumento = ArrayList<String>()
                    arrayTipoDocumento.add("Seleccione su Tipo de Documento")
                    arrayTipoDocumento.add("CTR-CARNET DE REFUGIADO")
                    arrayTipoDocumento.add("CEI-CÉDULA DE IDENTIDAD")
                    arrayTipoDocumento.add("DIT-DOC. IDENTI TEMPORAL")
                    arrayTipoDocumento.add("PAS-PASAPORTE")
                }
                val spinnerArrayTipoDocumentoAdapter = ArrayAdapter(
                    this@DatosPersonalesActivity,
                    android.R.layout.simple_spinner_dropdown_item,
                    arrayTipoDocumento
                )
                spinnerTipoDocumento.adapter = spinnerArrayTipoDocumentoAdapter
                spinnerTipoDocumento.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    //((TextView) adapterView.getChildAt(0)).setGravity(Gravity.CENTER)
                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        Toast.makeText(
                            this@DatosPersonalesActivity,
                            "onNothingSelected",
                            Toast.LENGTH_LONG,
                        ).show()
                    }
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        jsonData.addProperty(
                            "strTipoDocumento",
                            arrayTipoDocumento[position].toString()
                        )
                    }
                }
                jsonData.addProperty(
                    "strPais",
                    arrayPais[position].toString()
                )
            }
        }
        spinnerPais.setGravity(Gravity.CENTER_VERTICAL or Gravity.CENTER_HORIZONTAL)
        spinnerPais.setLayoutParams(generalLayout)
        buttonGuardarDatosPersonales.setOnClickListener {
            try {
                val pDialog = SweetAlertDialog(this@DatosPersonalesActivity, SweetAlertDialog.PROGRESS_TYPE)
                pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
                pDialog.titleText = "Cargando ..."
                pDialog.setCancelable(true)
                pDialog.show()
                val apiService = RetrofitHelper.getInstance().create(ApiService::class.java)

                jsonData.addProperty("intIdEncuesta", jsonParametros.get("idEncuesta").asInt)
                jsonData.addProperty("intNumeroDocumento", identificacionEditTextNumber.text.toString())
                jsonData.addProperty("strUsrSesion", "appMovil")
                val jsonObject = JsonObject()
                jsonObject.add("data", jsonData)
                Log.d("Interceptor", "Request: " + jsonObject)
                lifecycleScope.launchWhenCreated {
                    try {
                        val response = apiService.getDatosPersonales(jsonObject)
                        if (response.isSuccessful()) {
                            pDialog.hide()
                            Log.d("Interceptor","resultado del getDatosPersonales isSuccessful")
                            Log.d("Interceptor","Response: "+response.body().toString())
                            //if(response.body()?.intStatus==200 && response.body()?.arrayEmpresa?.isNotEmpty() == true)
                            if(response.body()?.intStatus==200 && response.body()?.jsonDatosPersona?.existe == "S") {
                                val politicaAceptada = response.body()!!.strPoliticaAceptada.toString()
                                val nombre   = response.body()!!.jsonDatosPersona.nombres.toString()
                                val ape_pat  = response.body()!!.jsonDatosPersona.ape_pat.toString()
                                val ape_mat  = response.body()!!.jsonDatosPersona.ape_mat.toString()
                                val correo   = response.body()!!.jsonDatosPersona.email.toString()
                                var genero   = response.body()!!.jsonDatosPersona.sexo.toString()
                                var fechaNac = response.body()!!.jsonDatosPersona.fechanac.toString()
                                genero = when (genero) {
                                    "M" -> "Masculino"
                                    "F" -> "Femenino"
                                    else -> "Sin Género"
                                }
                                val arrayFechaNac = fechaNac.split("-")
                                fechaNac = arrayFechaNac[0]
                                val nombreCompleto = nombre+" "+ape_pat+" "+ape_mat
                                var mensaje = "¿Usted es el paciente: "+nombreCompleto+" ?"
                                if (response.body()?.jsonDatosPersona?.habitacion?.toString()?.isNotEmpty() == true)
                                {
                                    Log.d("Interceptor","Entro en validacion de habitacion")
                                    val habitacion = response.body()!!.jsonDatosPersona.habitacion.toString()
                                    mensaje = "¿Usted es el paciente: "+nombreCompleto+", de la habitación: "+habitacion+" ?"
                                }
                                Log.d("Interceptor","nombreCompleto: "+nombreCompleto)
                                SweetAlertDialog(this@DatosPersonalesActivity, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText(mensaje)
                                    .setConfirmText("Aceptar")
                                    .setConfirmClickListener { sDialog -> sDialog.dismissWithAnimation()
                                        Log.d("Interceptor","Usuario confirmó sus datos")
                                        jsonParametros.addProperty("identificacionClt", identificacionEditTextNumber.text.toString())
                                        jsonParametros.addProperty("correoClt", correo)
                                        jsonParametros.addProperty("generoClt", genero)
                                        jsonParametros.addProperty("fechaNacClt", fechaNac)
                                        if(politicaAceptada=="Si")
                                        {
                                            getViewPregunta(jsonParametros)
                                        }
                                        else
                                        {
                                            getViewTratamientoDP(jsonParametros)
                                        }
                                    }
                                    .setCancelButton(
                                        "Cancelar"
                                    ) { sDialog -> sDialog.dismissWithAnimation() }
                                    .show()
                            }
                            else if(response.body()?.intStatus==200 && response.body()?.jsonDatosPersona?.existe == "N") {
                                pDialog.hide()
                                SweetAlertDialog(this@DatosPersonalesActivity, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Error")
                                    .setContentText("No hay Usuario registrado con los valores ingresados")
                                    .show()
                            }
                            else
                            {
                                pDialog.hide()
                                SweetAlertDialog(this@DatosPersonalesActivity, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Error")
                                    .setContentText(response.body()?.strMensaje.toString())
                                    .show()
                            }
                        }
                        else {
                            pDialog.hide()
                            SweetAlertDialog(this@DatosPersonalesActivity, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Error")
                                .setContentText("Ha ocurrido un error, al tratar de realizar conexión con el servidor")
                                .show()
                        }
                    } catch (Ex: Exception) {
                        pDialog.hide()
                        SweetAlertDialog(this@DatosPersonalesActivity, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error")
                            .setContentText(Ex.localizedMessage)
                            .show()
                        Log.e("Interceptor", Ex.localizedMessage)
                    }
                }

            } catch (Ex: Exception) {
                SweetAlertDialog(this@DatosPersonalesActivity, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error")
                    .setContentText(Ex.localizedMessage)
                    .show()
                Log.e("Interceptor", Ex.localizedMessage)
            }
        }
    }
    private fun getViewTratamientoDP(jsonParametros: JsonObject)
    {
        Log.d("Interceptor","----------------------")
        Log.d("Interceptor","--getViewTratamientoDP--")
        Log.d("Interceptor",jsonParametros.toString())
        val tratamientoActivityIntent = Intent(this, TratamientoDPActivity::class.java).apply()
        {
            Log.d("Interceptor","preparo datos")
            putExtra("idEncuesta",jsonParametros.get("idEncuesta").asInt)
            putExtra("titulo",jsonParametros.get("titulo").asString)
            putExtra("descripcion",jsonParametros.get("descripcion").asString)
            putExtra("permiteFirma",jsonParametros.get("permiteFirma").asString)
            putExtra("permiteDatoAdicional",jsonParametros.get("permiteDatoAdicional").asString)
            putExtra("tiempoDeEspera",jsonParametros.get("tiempoDeEspera").asInt)
            putExtra("email",jsonParametros.get("email").asString)
            putExtra("idEmpresa",jsonParametros.get("idEmpresa").asInt)
            //Datos del cliente
            putExtra("identificacionClt",jsonParametros.get("identificacionClt").asString)
            putExtra("correoClt",jsonParametros.get("correoClt").asString)
            putExtra("generoClt",jsonParametros.get("generoClt").asString)
            putExtra("fechaNacClt",jsonParametros.get("fechaNacClt").asString)
        }
        Log.d("Interceptor","envio datos")
        startActivity(tratamientoActivityIntent)
    }
    private fun getViewPregunta(jsonParametros: JsonObject)
    {
        Log.d("Interceptor","----------------------")
        Log.d("Interceptor","--getViewPregunta--")
        Log.d("Interceptor",jsonParametros.toString())
        val preguntaActivityIntent = Intent(this, PreguntaActivity::class.java).apply()
        {
            putExtra("idEncuesta",jsonParametros.get("idEncuesta").asInt)
            putExtra("titulo",jsonParametros.get("titulo").asString)
            putExtra("descripcion",jsonParametros.get("descripcion").asString)
            putExtra("permiteFirma",jsonParametros.get("permiteFirma").asString)
            putExtra("permiteDatoAdicional",jsonParametros.get("permiteDatoAdicional").asString)
            putExtra("tiempoDeEspera",jsonParametros.get("tiempoDeEspera").asInt)
            putExtra("email",jsonParametros.get("email").asString)
            putExtra("idEmpresa",jsonParametros.get("idEmpresa").asInt)
            //Datos del cliente
            putExtra("correoClt",jsonParametros.get("correoClt").asString)
            putExtra("generoClt",jsonParametros.get("generoClt").asString)
            putExtra("fechaNacClt",jsonParametros.get("fechaNacClt").asString)
        }
        startActivity(preguntaActivityIntent)
    }
}