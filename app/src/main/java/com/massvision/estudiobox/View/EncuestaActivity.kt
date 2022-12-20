package com.massvision.estudiobox.View

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Space
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.massvision.estudiobox.R
import com.massvision.estudiobox.Repository.ApiService
import com.massvision.estudiobox.Repository.RetrofitHelper
import org.json.JSONObject

class EncuestaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_encuesta)
        setup()
    }
    private fun setup()
    {
        title = "Encuestas"
        //consumo ApiRest
        Log.d("Interceptor","consumo ApiRest")
        val apiService = RetrofitHelper.getInstance().create(ApiService::class.java)
        val jsonObject = JSONObject()
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
                    val parametrosLayout = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    val parametrosLayoutTwo = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,16
                    )
                    if(response.body()?.intStatus==200 && response.body()?.arrayEncuesta?.isNotEmpty() == true)
                    {
                        Log.d("Interceptor","+++++++++++++++")
                        for(arrayItem in response.body()?.arrayEncuesta!!)
                        {
                            Log.d("Interceptor", "Titulo Encuesta: "+arrayItem.strTitulo)
                            val button = Button(this@EncuestaActivity)
                            button.setLayoutParams(parametrosLayout)
                            button.setText(arrayItem.strTitulo)
                            button.setBackgroundColor(Color.BLUE)
                            button.setTextColor(Color.WHITE)
                            button.setOnClickListener {
                                getViewPregunta(arrayItem.intIdEncuesta)
                            }
                            encuestaLayoutSecundario.addView(button)
                            val space = Space(this@EncuestaActivity)
                            space.setLayoutParams(parametrosLayoutTwo)
                            encuestaLayoutSecundario.addView(space)
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
        Toast.makeText(this@EncuestaActivity,"idEncuesta: "+idEncuesta, Toast.LENGTH_LONG).show()
        val preguntaActivityIntent = Intent(this, PreguntaActivity::class.java).apply()
        {
            putExtra("idEncuesta",idEncuesta)
        }
        startActivity(preguntaActivityIntent)
    }
}