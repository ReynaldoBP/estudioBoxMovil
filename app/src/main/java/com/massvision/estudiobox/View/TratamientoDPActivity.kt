package com.massvision.estudiobox.View

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Space
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.kyanogen.signatureview.SignatureView
import com.massvision.estudiobox.R
import com.massvision.estudiobox.Repository.ApiService
import com.massvision.estudiobox.Repository.RetrofitHelper
import kotlinx.android.synthetic.main.activity_tratamiento_dpactivity.*
import java.io.ByteArrayOutputStream

class TratamientoDPActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tratamiento_dpactivity)
        val bundle= intent.extras
        val idEncuesta = bundle?.getInt("idEncuesta")
        val titulo = bundle?.getString("titulo")
        val descripcion = bundle?.getString("descripcion")
        val permiteFirma = bundle?.getString("permiteFirma")
        val permiteDatoAdicional = bundle?.getString("permiteDatoAdicional")
        val tiempoDeEspera = bundle?.getInt("tiempoDeEspera")
        val idEmpresa = bundle?.getInt("idEmpresa")
        val email = bundle?.getString("email")
        //Datos del cliente
        val identificacionClt = bundle?.getString("identificacionClt")
        val correoClt = bundle?.getString("correoClt")
        val generoClt = bundle?.getString("generoClt")
        val fechaNacClt = bundle?.getString("fechaNacClt")
        if (idEncuesta != null && titulo!= null && descripcion!= null && permiteFirma!= null
            && permiteDatoAdicional!= null && tiempoDeEspera!= null && email!= null && idEmpresa!= null
            && identificacionClt != null && correoClt!= null && generoClt!= null && fechaNacClt!= null) {
            val jsonParametros = JsonObject()
            jsonParametros.addProperty("idEncuesta", idEncuesta)
            jsonParametros.addProperty("titulo", titulo)
            jsonParametros.addProperty("descripcion", descripcion)
            jsonParametros.addProperty("permiteFirma", permiteFirma)
            jsonParametros.addProperty("permiteDatoAdicional", permiteDatoAdicional)
            jsonParametros.addProperty("tiempoDeEspera", tiempoDeEspera)
            jsonParametros.addProperty("email", email)
            jsonParametros.addProperty("idEmpresa", idEmpresa)
            //Datos del cliente
            jsonParametros.addProperty("identificacionClt", identificacionClt)
            jsonParametros.addProperty("correoClt", correoClt)
            jsonParametros.addProperty("generoClt", generoClt)
            jsonParametros.addProperty("fechaNacClt", fechaNacClt)
            setup(jsonParametros)
        }
    }
    private fun setup(jsonParametros: JsonObject)
    {
        Log.d("Interceptor","----------------------")
        Log.d("Interceptor","--TratamientoDPActivity--")
        Log.d("Interceptor",jsonParametros.toString())
        title = "Tratamiento de Datos Personales"
        val pDialog = SweetAlertDialog(this@TratamientoDPActivity, SweetAlertDialog.PROGRESS_TYPE)
        pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
        pDialog.titleText = "Cargando ..."
        pDialog.setCancelable(true)
        pDialog.show()
        //consumo ApiRest
        Log.d("Interceptor","consumo ApiRest")
        val apiService = RetrofitHelper.getInstance().create(ApiService::class.java)
        val jsonData = JsonObject()
        jsonData.addProperty("intIdEmpresa",jsonParametros.get("idEmpresa").asInt)
        val jsonObject = JsonObject()
        jsonObject.add("data",jsonData)

        Log.d("Interceptor","Request: "+jsonObject)
        lifecycleScope.launchWhenCreated {
            try {
                val response = apiService.getTratamientoDP(jsonObject)
                if (response.isSuccessful()) {
                    pDialog.hide()
                    Log.d("Interceptor", "resultado del getTratamientoDP isSuccessful")
                    Log.d("Interceptor", "Response: " + response.body().toString())
                    //Establecemos el layout
                    setContentView(R.layout.activity_tratamiento_dpactivity)
                    //Obtenemos el linear layout donde colocar los datos
                    //Creamos el layaut que va a contener el card view
                    val params = RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    params.setMargins(16, 16, 16, 16)
                    val tratamientoLayoutSecundario =
                        findViewById<LinearLayout>(R.id.tratamientoLayoutSecundario)
                    val textTitulo = TextView(this@TratamientoDPActivity)
                    textTitulo.text =
                        "Nos interesa tu privacidad y la protección de la información. Ayúdanos confirmando lo siguiente:";
                    textTitulo.textSize = 25f
                    textTitulo.setTextColor(Color.RED)
                    textTitulo.setTypeface(Typeface.SANS_SERIF, Typeface.NORMAL)
                    textTitulo.layoutParams = params
                    tratamientoLayoutSecundario.addView(textTitulo)
                    //Creamos las propiedades de layout que tendrán los botones.
                    val parametrosLayoutTwo = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, 16
                    )
                    if (response.body()?.intStatus == 200 && response.body()?.arrayTratamientoDP?.isNotEmpty() == true) {
                        Log.d("Interceptor", "+++++++++++++++")
                        val spaceLayout = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, 16
                        )
                        //Creamos las propiedades de layout de forma general
                        val generalLayout = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        val arrayJsonTratamientoDP = JsonArray()
                        for (arrayItem in response.body()?.arrayTratamientoDP!!) {
                            Log.d("Interceptor", "Descripción: " + arrayItem.strDescripcion)
                            //Guardamos los id en un array
                            val item = JsonObject()
                            item.addProperty("intIdTratamientoDatosPersonales", arrayItem.intIdTratamientoDatosPersonales)
                            arrayJsonTratamientoDP.add(item)
                            /*--------------*/
                            //Creamos el layaut que va a contener el card view
                            val cardLinearLayout = LinearLayout(this@TratamientoDPActivity)
                            cardLinearLayout.orientation = LinearLayout.VERTICAL
                            val params = RelativeLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                            )
                            params.setMargins(16, 16, 16, 16)
                            //Creamos el cardView
                            val cardView = CardView(this@TratamientoDPActivity)
                            cardView.radius = 15f
                            cardView.radius = 25f
                            val color = ContextCompat.getColor(
                                this@TratamientoDPActivity,
                                android.R.color.white
                            )
                            cardView.setCardBackgroundColor(color)
                            cardView.setContentPadding(36, 36, 36, 36)
                            cardView.layoutParams = params
                            //cardView.cardElevation = 30f
                            //Creamos el texto
                            val textDescripcion = TextView(this@TratamientoDPActivity)
                            textDescripcion.text = arrayItem.strDescripcion;
                            textDescripcion.textSize = 20f
                            textDescripcion.setTextColor(Color.BLACK)
                            textDescripcion.setTypeface(Typeface.SANS_SERIF, Typeface.NORMAL)
                            //Agregamos los Layout
                            cardLinearLayout.addView(textDescripcion)
                            //Creamos Texto para la Sucursal
                            if (arrayItem.strUrl != "") {
                                val textUrl = TextView(this@TratamientoDPActivity)
                                textUrl.text = "Ver política de Privacidad"
                                textUrl.textSize = 17f
                                textUrl.setTypeface(Typeface.MONOSPACE, Typeface.ITALIC)
                                val spaceUrl = Space(this@TratamientoDPActivity)
                                spaceUrl.setLayoutParams(spaceLayout)
                                cardLinearLayout.addView(textUrl)
                            }
                            cardView.setOnClickListener {
                                if (arrayItem.strUrl != "") {
                                    val intent = Intent(Intent.ACTION_VIEW)
                                    intent.data = Uri.parse(arrayItem.strUrl)
                                    startActivity(intent)
                                }
                            }
                            cardView.addView(cardLinearLayout)
                            //Agregamos el cardView a nuestro layout
                            tratamientoLayoutSecundario.addView(cardView)
                            //Bloque que permite crear un registro del tratamiento de Datos
                            buttonGuardarTratamientoDP.setOnClickListener {
                                Log.d("Interceptor", "----------------------")
                                Log.d("Interceptor", "click en boton")
                                //Guardarmos la firma
                                val signatureBitmap = firmaSignatureView.getSignatureBitmap()
                                // Convertir el bitmap a una cadena Base64
                                val byteArrayOutputStream = ByteArrayOutputStream()
                                signatureBitmap.compress(
                                    Bitmap.CompressFormat.PNG,
                                    100,
                                    byteArrayOutputStream
                                )
                                val byteArray = byteArrayOutputStream.toByteArray()
                                val base64String = Base64.encodeToString(byteArray, Base64.DEFAULT)
                                // Ahora puedes enviar 'base64String' al backend
                                if (firmaSignatureView.isBitmapEmpty()) {
                                    Log.d("Interceptor", "Firma vacía")
                                    //jsonDataRespuesta.addProperty("strFirma", "")
                                    SweetAlertDialog(this@TratamientoDPActivity, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Error")
                                        .setContentText("Estimado Usuario la firma es un campo obligatorio")
                                        .show()
                                } else {
                                    Log.d("Interceptor", "Firma llena")
                                    val pDialog = SweetAlertDialog(this@TratamientoDPActivity, SweetAlertDialog.PROGRESS_TYPE)
                                    pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
                                    pDialog.titleText = "Cargando ..."
                                    pDialog.setCancelable(true)
                                    pDialog.show()
                                    //consumo ApiRest
                                    Log.d("Interceptor","consumo ApiRest para registrar las políticas de seguridad")
                                    val apiServiceTratamientoDP = RetrofitHelper.getInstance().create(ApiService::class.java)
                                    val jsonDataTratamientoDP = JsonObject()
                                    jsonDataTratamientoDP.add("arrayTratamientoDP", arrayJsonTratamientoDP)
                                    jsonDataTratamientoDP.addProperty("strFirma", base64String)
                                    jsonDataTratamientoDP.addProperty("strIdentificacion",jsonParametros.get("identificacionClt").asString)
                                    jsonDataTratamientoDP.addProperty("strCorreo",jsonParametros.get("correoClt").asString)
                                    jsonDataTratamientoDP.addProperty("strUsrSesion",jsonParametros.get("email").asString)
                                    val jsonObjectTratamientoDP = JsonObject()
                                    jsonObjectTratamientoDP.add("data",jsonDataTratamientoDP)
                                    Log.d("Interceptor",jsonObjectTratamientoDP.toString())
                                    lifecycleScope.launchWhenCreated {
                                        try {
                                            val response = apiServiceTratamientoDP.createTratamientoDP(jsonObjectTratamientoDP)
                                            if (response.isSuccessful()) {
                                                pDialog.hide()
                                                getViewPregunta(jsonParametros)
                                            }else {
                                                pDialog.hide()
                                                SweetAlertDialog(this@TratamientoDPActivity, SweetAlertDialog.ERROR_TYPE)
                                                    .setTitleText("Error")
                                                    .setContentText("Ha ocurrido un error, por favor inténtalo de nuevo más tarde")
                                                    .show()
                                            }
                                        }catch (Ex: Exception) {
                                            pDialog.hide()
                                            SweetAlertDialog(this@TratamientoDPActivity, SweetAlertDialog.ERROR_TYPE)
                                                .setTitleText("Error")
                                                .setContentText(Ex.localizedMessage)
                                                .show()
                                            Log.e("Interceptor",Ex.localizedMessage)
                                        }
                                    }
                                }
                            }
                            Log.d("Interceptor", "----------------------")
                        }
                        //Configuramos para que aparezca la firma
                        val firmaCardView = findViewById<CardView>(R.id.firmaCardView)
                        firmaCardView.visibility = View.VISIBLE
                        //Establecemos un texto de ayuda
                        val textFirma = TextView(this@TratamientoDPActivity)
                        textFirma.textSize = 17f
                        textFirma.setGravity(Gravity.LEFT or Gravity.LEFT)
                        textFirma.setLayoutParams(generalLayout)
                        textFirma.setText("Ingrese su Firma:")
                        textFirma.setBackgroundColor(Color.TRANSPARENT)
                        textFirma.setTextColor(Color.BLACK)
                        textFirma.setTypeface(null, Typeface.BOLD)// Establecer estilo negrita
                        firmaCardView.addView(textFirma)
                        //Instanciamos el objeto de la firma para que aparezca
                        val firmaSignatureView = findViewById<SignatureView>(R.id.firmaSignatureView)
                        firmaSignatureView.visibility = View.VISIBLE
                    } else if (response.body()?.intStatus == 204) {
                        pDialog.hide()
                        SweetAlertDialog(this@TratamientoDPActivity, SweetAlertDialog.WARNING_TYPE)
                            //.setTitleText("Are you sure?")
                            .setContentText(response.body()?.strMensaje.toString())
                            .setConfirmText("Aceptar")
                            .setConfirmClickListener {
                                getViewEmpresa(jsonParametros.get("email").asString)
                            }.show()
                    } else {
                        pDialog.hide()
                        SweetAlertDialog(this@TratamientoDPActivity, SweetAlertDialog.WARNING_TYPE)
                            //.setTitleText("Are you sure?")
                            .setContentText(response.body()?.strMensaje.toString())
                            .setConfirmText("Aceptar")
                            .setConfirmClickListener {
                                getViewEmpresa(jsonParametros.get("email").asString)
                            }.show()
                    }
                } else {
                    pDialog.hide()
                    SweetAlertDialog(this@TratamientoDPActivity, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Error")
                        .setContentText("Ha ocurrido un error, por favor inténtalo de nuevo más tarde")
                        .show()
                }
            } catch (Ex: Exception) {
                pDialog.hide()
                SweetAlertDialog(this@TratamientoDPActivity, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error")
                    .setContentText("Ha ocurrido un error, por favor inténtalo de nuevo más tarde")
                    .show()
                Log.e("Interceptor", Ex.localizedMessage)
            }
        }
    }
    private fun getViewEmpresa(email:String)
    {
        val empresaActivityIntent = Intent(this, EmpresaActivity::class.java).apply()
        {
            putExtra("email",email)
        }
        startActivity(empresaActivityIntent)
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