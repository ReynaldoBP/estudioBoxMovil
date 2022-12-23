package com.massvision.estudiobox.View

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
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
        if (idEmpresa != null) {
            setup(idEmpresa)
        }
    }
    private fun setup(idEmpresa:Int)
    {
        title = "Encuestas"
        //consumo ApiRest
        Log.d("Interceptor","consumo ApiRest")
        Log.d("Interceptor", "idEmpresa: "+idEmpresa)
        val apiService = RetrofitHelper.getInstance().create(ApiService::class.java)
        val jsonData = JsonObject()
        jsonData.addProperty("intIdEmpresa",idEmpresa)
        val jsonObject = JsonObject()
        jsonObject.add("data",jsonData)

        Log.d("Interceptor","Request: "+jsonObject)
        lifecycleScope.launchWhenCreated {
            try {
                val response = apiService.getEncuesta(jsonObject)
                if (response.isSuccessful()) {
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
                            val color = ContextCompat.getColor(this@EncuestaActivity, com.google.android.material.R.color.design_default_color_primary_variant)
                            cardView.setCardBackgroundColor(color)
                            cardView.setContentPadding(36,36,36,36)
                            cardView.layoutParams = params
                            //cardView.cardElevation = 30f
                            //Creamos el texto
                            val textEncuesta = TextView(this@EncuestaActivity)
                            textEncuesta.text = "Título: "+arrayItem.strTitulo;
                            textEncuesta.textSize = 20f
                            textEncuesta.setTextColor(Color.WHITE)
                            textEncuesta.setTypeface(Typeface.SANS_SERIF, Typeface.NORMAL)
                            //Creamos otro texto
                            val textDescEncuesta = TextView(this@EncuestaActivity)
                            textDescEncuesta.text = "Area: "+arrayItem.strArea
                            textDescEncuesta.textSize = 17f
                            textDescEncuesta.setTypeface(Typeface.MONOSPACE, Typeface.ITALIC)
                            textDescEncuesta.setTextColor(Color.parseColor("#E0F2F1"))
                            val space = Space(this@EncuestaActivity)
                            space.setLayoutParams(spaceLayout)

                            //Creamos otro texto para ver las encuestas
                            val textVerPreguntas = TextView(this@EncuestaActivity)
                            textVerPreguntas.text = "Iniciar Encuesta >>"
                            textVerPreguntas.textSize = 14f
                            textVerPreguntas.setTypeface(Typeface.MONOSPACE, Typeface.ITALIC)
                            textVerPreguntas.setTextColor(Color.WHITE)
                            cardLinearLayout.addView(textEncuesta)
                            cardLinearLayout.addView(textDescEncuesta)
                            cardLinearLayout.addView(space)
                            cardLinearLayout.addView(textVerPreguntas)
                            cardView.setOnClickListener {
                                getViewPregunta(arrayItem.intIdEncuesta)
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
                    }

                } else {
                    Toast.makeText(this@EncuestaActivity,"Error al consumir Web Services getEncuesta", Toast.LENGTH_LONG).show()
                }
            }catch (Ex:Exception){
                Log.e("Error",Ex.localizedMessage)
            }
        }
    }
    private fun getViewPregunta(idEncuesta: Int)
    {
        val preguntaActivityIntent = Intent(this, PreguntaActivity::class.java).apply()
        {
            putExtra("idEncuesta",idEncuesta)
        }
        startActivity(preguntaActivityIntent)
    }
}