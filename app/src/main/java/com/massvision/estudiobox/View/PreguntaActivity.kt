package com.massvision.estudiobox.View

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Space
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.google.gson.JsonObject
import com.massvision.estudiobox.R
import com.massvision.estudiobox.Repository.ApiService
import com.massvision.estudiobox.Repository.RetrofitHelper
import kotlinx.android.synthetic.main.activity_empresa.*
import org.json.JSONObject

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
        title = "Listado de Preguntas"
        Log.d("Interceptor", "idEncuesta: "+idEncuesta)
        //consumo ApiRest
        Log.d("Interceptor","consumo ApiRest")
        val apiService = RetrofitHelper.getInstance().create(ApiService::class.java)
        val jsonData = JsonObject()
        jsonData.addProperty("intIdEncuesta",idEncuesta)
        val jsonParametro = JsonObject()
        jsonParametro.add("data",jsonData)
        lifecycleScope.launchWhenCreated {
            try {
                val response = apiService.getPregunta(jsonParametro)
                if (response.isSuccessful()) {
                    Log.d("Interceptor", "resultado del getPregunta isSuccessful")
                    Log.d("Interceptor", "Response: " + response.body().toString())
                    //Establecemos el layout
                    setContentView(R.layout.activity_pregunta)
                    //Obtenemos el linear layout donde colocar los botones
                    val preguntaLayoutSecundario =
                        findViewById<LinearLayout>(R.id.preguntaLayoutSecundario)
                    //Creamos las propiedades de layout que tendrán los botones.
                    val parametrosLayout = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    val parametrosLayoutTwo = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, 16
                    )
                    if (response.body()?.intStatus == 200 && response.body()?.arrayPregunta?.isNotEmpty() == true) {
                        Log.d("Interceptor", "+++++++++++++++")
                        for (arrayItem in response.body()?.arrayPregunta!!) {
                            Log.d("Interceptor", "Pregunta: " + arrayItem.strPregunta)
                            val button = Button(this@PreguntaActivity)
                            button.setLayoutParams(parametrosLayout)
                            button.setText(arrayItem.strPregunta)
                            button.setBackgroundColor(Color.BLUE)
                            button.setTextColor(Color.WHITE)
                            preguntaLayoutSecundario.addView(button)
                            val space = Space(this@PreguntaActivity)
                            space.setLayoutParams(parametrosLayoutTwo)
                            preguntaLayoutSecundario.addView(space)
                            Log.d("Interceptor", "----------------------")
                        }
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
}