package com.massvision.estudiobox.View

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.gson.JsonObject
import com.massvision.estudiobox.R
import com.massvision.estudiobox.Repository.ApiService
import com.massvision.estudiobox.Repository.RetrofitHelper
import org.json.JSONObject
class EncuestaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_encuesta)
        val bundle= intent.extras
        val idEmpresa = bundle?.getInt("idEmpresa")
        val idCliente = bundle?.getInt("idCliente")
        val email = bundle?.getString("email")
        if (idEmpresa != null && email != null && idCliente != null) {
            setup(idEmpresa,email,idCliente)
        }
        Log.d("Interceptor", "idEmpresa: " + idEmpresa + " email:" + email)
    }

    private fun setup(idEmpresa:Int,email:String,idCliente:Int)
    {
        title = "Encuestas"
        val pDialog = SweetAlertDialog(this@EncuestaActivity, SweetAlertDialog.PROGRESS_TYPE)
        pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
        pDialog.titleText = "Cargando ..."
        pDialog.setCancelable(true)
        pDialog.show()
        //consumo ApiRest
        Log.d("Interceptor","consumo ApiRest")
        Log.d("Interceptor", "idEmpresa: "+idEmpresa)
        Log.d("Interceptor", "idCliente: "+idCliente)

        val apiService = RetrofitHelper.getInstance().create(ApiService::class.java)
        val jsonData = JsonObject()
        jsonData.addProperty("intIdEmpresa",idEmpresa)
        jsonData.addProperty("intIdCliente",idCliente)
        val jsonObject = JsonObject()
        jsonObject.add("data",jsonData)

        Log.d("Interceptor","Request: "+jsonObject)
        lifecycleScope.launchWhenCreated {
            try {
                val response = apiService.getEncuesta(jsonObject)
                if (response.isSuccessful()) {
                    pDialog.hide()
                    Log.d("Interceptor","resultado del getEncuesta isSuccessful")
                    Log.d("Interceptor","Response: "+response.body().toString())
                    //Establecemos el layout
                    setContentView(R.layout.activity_encuesta)
                    //Obtenemos el linear layout donde colocar los botones
                    val encuestaLayoutSecundario = findViewById<LinearLayout>(R.id.encuestaLayoutSecundario)
                    //Creamos las propiedades de layout que tendrán los botones.
                    val parametrosLayoutTwo = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,16
                    )
                    if(response.body()?.intStatus==200 && response.body()?.arrayEncuesta?.isNotEmpty() == true)
                    {
                        Log.d("Interceptor","+++++++++++++++")
                        val spaceLayout = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, 16
                        )
                        for(arrayItem in response.body()?.arrayEncuesta!!)
                        {
                            Log.d("Interceptor", "Titulo Encuesta: "+arrayItem.strTitulo)
                            /*--------------*/
                            //Creamos el layaut que va a contener el card view
                            val cardLinearLayout = LinearLayout(this@EncuestaActivity)
                            cardLinearLayout.orientation = LinearLayout.VERTICAL
                            val params = RelativeLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT)
                            params.setMargins(16,16,16,16)
                            //Creamos el cardView
                            val cardView = CardView(this@EncuestaActivity)
                            cardView.radius = 15f
                            cardView.radius = 25f
                            val color = ContextCompat.getColor(this@EncuestaActivity, android.R.color.white)
                            cardView.setCardBackgroundColor(color)
                            cardView.setContentPadding(36,36,36,36)
                            cardView.layoutParams = params
                            //cardView.cardElevation = 30f
                            //Creamos el texto
                            val textEncuesta = TextView(this@EncuestaActivity)
                            textEncuesta.text = "Título: "+arrayItem.strTitulo;
                            textEncuesta.textSize = 20f
                            textEncuesta.setTextColor(Color.BLACK)
                            textEncuesta.setTypeface(Typeface.SANS_SERIF, Typeface.NORMAL)
                            //Creamos Texto para la Sucursal
                            val textSucursalEncuesta = TextView(this@EncuestaActivity)
                            textSucursalEncuesta.text = "Sucursal: "+arrayItem.strSucursal
                            textSucursalEncuesta.textSize = 17f
                            textSucursalEncuesta.setTypeface(Typeface.MONOSPACE, Typeface.ITALIC)
                            textEncuesta.setTextColor(Color.BLACK)
                            val spaceSucursal = Space(this@EncuestaActivity)
                            spaceSucursal.setLayoutParams(spaceLayout)
                            //Creamos Texto para las areas
                            val textAreaEncuesta = TextView(this@EncuestaActivity)
                            textAreaEncuesta.text = "Area: "+arrayItem.strArea
                            textAreaEncuesta.textSize = 17f
                            textAreaEncuesta.setTypeface(Typeface.MONOSPACE, Typeface.ITALIC)
                            textEncuesta.setTextColor(Color.BLACK)
                            val space = Space(this@EncuestaActivity)
                            space.setLayoutParams(spaceLayout)

                            //Creamos otro texto para ver las encuestas
                            val textVerPreguntas = TextView(this@EncuestaActivity)
                            textVerPreguntas.text = "Iniciar Encuesta >>"
                            textVerPreguntas.textSize = 14f
                            textVerPreguntas.setTypeface(Typeface.MONOSPACE, Typeface.ITALIC)
                            textVerPreguntas.setTextColor(Color.BLACK)
                            cardLinearLayout.addView(textEncuesta)
                            cardLinearLayout.addView(textSucursalEncuesta)
                            cardLinearLayout.addView(textAreaEncuesta)
                            cardLinearLayout.addView(space)
                            cardLinearLayout.addView(textVerPreguntas)
                            cardView.setOnClickListener {
                                //1 Empresa Artefacta de prueba / Kennedy 11
                                if(idEmpresa==100)
                                {
                                    getViewDatosPersonales(arrayItem.intIdEncuesta,arrayItem.strTitulo,arrayItem.strDescripcion,arrayItem.strPermiteFirma,arrayItem.strPermiteDatoAdicional,arrayItem.intTiempo,email,idEmpresa)
                                }
                                else
                                {
                                    getViewPregunta(arrayItem.intIdEncuesta,arrayItem.strTitulo,arrayItem.strDescripcion,arrayItem.strPermiteFirma,arrayItem.strPermiteDatoAdicional,arrayItem.intTiempo,email,idEmpresa)
                                }
                            }
                            cardView.addView(cardLinearLayout)
                            //Agregamos el cardView a nuestro layout
                            encuestaLayoutSecundario.addView(cardView)
                            //Ingresamos un espacio
                            /*val space = Space(this@EncuestaActivity)
                            space.setLayoutParams(parametrosLayoutTwo)
                            encuestaLayoutSecundario.addView(space)*/
                            Log.d("Interceptor","----------------------")
                        }
                    }else if(response.body()?.intStatus==204)
                    {
                        pDialog.hide()
                        SweetAlertDialog(this@EncuestaActivity, SweetAlertDialog.WARNING_TYPE)
                            //.setTitleText("Are you sure?")
                            .setContentText(response.body()?.strMensaje.toString())
                            .setConfirmText("Aceptar")
                            .setConfirmClickListener {
                                getViewEmpresa(email)
                            }.show()
                    }else {
                        pDialog.hide()
                        SweetAlertDialog(this@EncuestaActivity, SweetAlertDialog.WARNING_TYPE)
                            //.setTitleText("Are you sure?")
                            .setContentText(response.body()?.strMensaje.toString())
                            .setConfirmText("Aceptar")
                            .setConfirmClickListener {
                                getViewEmpresa(email)
                            }.show()
                    }
                }
                else {
                        pDialog.hide()
                        SweetAlertDialog(this@EncuestaActivity, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error")
                            .setContentText("Ha ocurrido un error, por favor inténtalo de nuevo más tarde")
                            .show()
                    }
            }catch (Ex:Exception){
                pDialog.hide()
                SweetAlertDialog(this@EncuestaActivity, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error")
                    .setContentText("Ha ocurrido un error, por favor inténtalo de nuevo más tarde")
                    .show()
                Log.e("Interceptor",Ex.localizedMessage)
            }
        }
    }
    private fun getViewDatosPersonales(idEncuesta: Int,titulo:String,descripcion:String,strPermiteFirma:String,strPermiteDatoAdicional:String,intTiempo: Int,email:String,idEmpresa: Int)
    {
        val datosPersonalesActivityIntent = Intent(this, DatosPersonalesActivity::class.java).apply()
        {
            putExtra("idEncuesta",idEncuesta)
            putExtra("titulo",titulo)
            putExtra("descripcion",descripcion)
            putExtra("permiteFirma",strPermiteFirma)
            putExtra("permiteDatoAdicional",strPermiteDatoAdicional)
            putExtra("tiempoDeEspera",intTiempo)
            putExtra("email",email)
            putExtra("idEmpresa",idEmpresa)

        }
        startActivity(datosPersonalesActivityIntent)
    }
    private fun getViewPregunta(idEncuesta: Int,titulo:String,descripcion:String,strPermiteFirma:String,strPermiteDatoAdicional:String,intTiempo: Int,email:String,idEmpresa:Int)
    {
        val preguntaActivityIntent = Intent(this, PreguntaActivity::class.java).apply()
        {
            putExtra("idEncuesta",idEncuesta)
            putExtra("titulo",titulo)
            putExtra("descripcion",descripcion)
            putExtra("permiteFirma",strPermiteFirma)
            putExtra("permiteDatoAdicional",strPermiteDatoAdicional)
            putExtra("tiempoDeEspera",intTiempo)
            putExtra("email",email)
            putExtra("idEmpresa",idEmpresa)
            //Datos del cliente
            putExtra("correoClt","")
            putExtra("generoClt","")
            putExtra("fechaNacClt","")
        }
        startActivity(preguntaActivityIntent)
    }
    private fun getViewEmpresa(email:String)
    {
        val empresaActivityIntent = Intent(this, EmpresaActivity::class.java).apply()
        {
            putExtra("email",email)
        }
        startActivity(empresaActivityIntent)
    }
}