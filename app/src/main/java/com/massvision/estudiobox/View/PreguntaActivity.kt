package com.massvision.estudiobox.View

import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.Gravity.CENTER
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.google.gson.JsonObject
import com.massvision.estudiobox.R
import com.massvision.estudiobox.Repository.ApiService
import com.massvision.estudiobox.Repository.RetrofitHelper
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_pregunta.*


class PreguntaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pregunta)
        val bundle= intent.extras
        //setup(email?:"",provider?:"")
        val idEncuesta = bundle?.getInt("idEncuesta")
        if (idEncuesta != null) {
            setup(idEncuesta)
        }
    }
    private fun setup(idEncuesta:Int)
    {
        title = "Preguntas"
        Log.d("Interceptor", "idEncuesta: "+idEncuesta)
        //consumo ApiRest
        Log.d("Interceptor","consumo ApiRest")
        val apiService = RetrofitHelper.getInstance().create(ApiService::class.java)
        val jsonData = JsonObject()
        jsonData.addProperty("intIdEncuesta",idEncuesta)
        val jsonObject = JsonObject()
        jsonObject.add("data",jsonData)
        lifecycleScope.launchWhenCreated {
            try {
                val response = apiService.getPregunta(jsonObject)
                if (response.isSuccessful()) {
                    Log.d("Interceptor", "resultado del getPregunta isSuccessful")
                    Log.d("Interceptor", "Response: " + response.body().toString())
                    //Establecemos el layout
                    setContentView(R.layout.activity_pregunta)
                    //Obtenemos el linear layout donde colocar los botones
                    val preguntaLayoutSecundario =
                        findViewById<LinearLayout>(R.id.preguntaLayoutSecundario)
                    //Creamos las propiedades de layout que tendrán los botones.
                    val buttonLayout = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    val spaceLayout = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, 16
                    )
                    if (response.body()?.intStatus == 200 && response.body()?.arrayPregunta?.isNotEmpty() == true) {
                        Log.d("Interceptor", "+++++++++++++++")
                        val jsonPregunta = JsonObject()
                        //Recorremos las preguntas
                        for (arrayItem in response.body()?.arrayPregunta!!) {
                            Log.d("Interceptor", "Pregunta: " + arrayItem.strPregunta)
                            //Presentamos las preguntas
                            val button = Button(this@PreguntaActivity)
                            button.setLayoutParams(buttonLayout)
                            button.setText(arrayItem.strPregunta)
                            button.setBackgroundColor(Color.TRANSPARENT)
                            button.setTextColor(Color.BLACK)
                            preguntaLayoutSecundario.addView(button)
                            //Presentamos las estrellas
                            if (arrayItem.strTipoOpcionRespuesta == "CERRADA")
                            {
                                val ratingBar: RatingBar = RatingBar(this@PreguntaActivity).apply {
                                    id = arrayItem.intIdPregunta
                                    setIsIndicator(false)
                                    numStars = arrayItem.intCantidadEstrellas
                                    stepSize = 1.0f
                                    layoutParams = LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.WRAP_CONTENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT
                                    ).apply { gravity = CENTER }
                                }
                                ratingBar.onRatingBarChangeListener =
                                    RatingBar.OnRatingBarChangeListener { _, rating, _ ->
                                        jsonPregunta.addProperty(
                                            ratingBar.id.toString(),
                                            rating.toInt()
                                        )
                                        Toast.makeText(
                                            this@PreguntaActivity, "Id:" + ratingBar.id + " Stars: " +
                                                    rating.toInt(), Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                //ratingBar.progressBackgroundTintList(Color.BLUE)
                                preguntaLayoutSecundario.addView(ratingBar)
                            }
                            //Presentamos texto para las preguntas con comentarios
                            else
                            {
                                val text = EditText(this@PreguntaActivity)
                                text.id = arrayItem.intIdPregunta
                                text.addTextChangedListener {
                                    jsonPregunta.addProperty(
                                        text.id.toString(),
                                        text.getText().toString()
                                    )
                                }
                                preguntaLayoutSecundario.addView(text)
                            }
                            val space = Space(this@PreguntaActivity)
                            space.setLayoutParams(spaceLayout)
                            preguntaLayoutSecundario.addView(space)
                            Log.d("Interceptor", "----------------------")
                        }
                        val jsonDataRespuesta = JsonObject()
                        jsonDataRespuesta.addProperty("intIdEncuesta",idEncuesta)
                        jsonDataRespuesta.add("arrayPregunta",jsonPregunta)
                        //Datos Adicionales
                        val buttonSeparador = Button(this@PreguntaActivity)
                        buttonSeparador.setLayoutParams(buttonLayout)
                        buttonSeparador.setText("Datos Adicionales")
                        buttonSeparador.setBackgroundColor(Color.TRANSPARENT)
                        buttonSeparador.setTextColor(Color.BLACK)
                        preguntaLayoutSecundario.addView(buttonSeparador)
                        //Correo electronico del encuestado
                        val textCorreo = EditText(this@PreguntaActivity)
                        textCorreo.setInputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
                        textCorreo.setHint("Correo Electrónico")
                        textCorreo.addTextChangedListener {
                            jsonDataRespuesta.addProperty(
                                "strCorreo",
                                textCorreo.getText().toString()
                            )
                        }
                        preguntaLayoutSecundario.addView(textCorreo)
                        //Guardar Encuesta
                        val buttonGuardarEncuesta = Button(this@PreguntaActivity)
                        buttonGuardarEncuesta.setLayoutParams(buttonLayout)
                        buttonGuardarEncuesta.setText("Guardar Encuesta")
                        buttonGuardarEncuesta.setBackgroundColor(Color.BLUE)
                        buttonGuardarEncuesta.setTextColor(Color.WHITE)
                        buttonGuardarEncuesta.setOnClickListener {
                            jsonDataRespuesta.addProperty("strGenero","FEMENINO")
                            jsonDataRespuesta.addProperty("strEdad","1975-07-23")
                            val jsonObjectRespuesta = JsonObject()
                            jsonObjectRespuesta.add("data",jsonDataRespuesta)
                            createRespuesta(jsonObjectRespuesta)
                        }
                        preguntaLayoutFooter.addView(buttonGuardarEncuesta)
                    } else {
                        Toast.makeText(
                            this@PreguntaActivity,
                            "Error al consumir Web Services getPregunta",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@PreguntaActivity,
                        "Error al consumir Web Services getPregunta",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (Ex: Exception) {
                Log.e("Error", Ex.localizedMessage)
            }
        }
    }
    private fun createRespuesta(jsonObject:JsonObject)
    {
        Log.d("Interceptor", "Request Respuesta: " + jsonObject)
        Toast.makeText(
            this@PreguntaActivity,
            "Encuesta Guardada",
            Toast.LENGTH_LONG
        ).show()
        /*val apiService = RetrofitHelper.getInstance().create(ApiService::class.java)
        val response = apiService.createRespuesta(jsonObject)*/
    }
}
