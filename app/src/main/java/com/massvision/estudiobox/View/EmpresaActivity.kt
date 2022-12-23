package com.massvision.estudiobox.View

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.JsonObject
import com.massvision.estudiobox.R
import com.massvision.estudiobox.Repository.ApiService
import com.massvision.estudiobox.Repository.RetrofitHelper
import kotlinx.android.synthetic.main.activity_empresa.*

class EmpresaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_empresa)
        //Logica para ingresar por correo-firebase
        val bundle= intent.extras
        val email = bundle?.getString("email")
        val provider = bundle?.getString("provider")
        setup()
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email",email)
        prefs.putString("provider",provider)
        prefs.apply()
    }
    private fun setup()
    {
        title = "Empresas"
        //consumo ApiRest
        Log.d("Interceptor","consumo ApiRest")
        val apiService = RetrofitHelper.getInstance().create(ApiService::class.java)
        val jsonObject = JsonObject()
        lifecycleScope.launchWhenCreated {
            try {
                val response = apiService.getEmpresa(jsonObject)
                if (response.isSuccessful()) {
                    Log.d("Interceptor","resultado del getEmpresa isSuccessful")
                    Log.d("Interceptor","Response: "+response.body().toString())
                    //Establecemos el layout
                    setContentView(R.layout.activity_empresa)
                    //Obtenemos el linear layout donde colocar los botones
                    val empresaLayoutSecundario = findViewById<LinearLayout>(R.id.empresaLayoutSecundario)
                    //Creamos las propiedades de layout que tendrán los botones.
                    val buttonLayout = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    if(response.body()?.intStatus==200 && response.body()?.arrayEmpresa?.isNotEmpty() == true)
                    {
                        Log.d("Interceptor","+++++++++++++++")
                        for(arrayItem in response.body()?.arrayEmpresa!!)
                        {
                            Log.d("Interceptor", "Empresa: "+arrayItem.strNombreComercial)
                            //Creamos el layaut que va a contener el card view
                            val cardLinearLayout = LinearLayout(this@EmpresaActivity)
                            cardLinearLayout.orientation = LinearLayout.VERTICAL
                            val params = RelativeLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT)
                            params.setMargins(16,16,16,16)
                            //Creamos el cardView
                            val cardView = CardView(this@EmpresaActivity)
                            cardView.radius = 15f
                            cardView.setCardBackgroundColor(Color.parseColor("#165de0"))
                            cardView.setContentPadding(36,36,36,36)
                            cardView.layoutParams = params
                            cardView.cardElevation = 30f
                            //Creamos el texto
                            val textEncuesta = TextView(this@EmpresaActivity)
                            textEncuesta.text = arrayItem.strNombreComercial;
                            textEncuesta.textSize = 18f
                            textEncuesta.setTextColor(Color.WHITE)
                            textEncuesta.setTypeface(Typeface.SANS_SERIF, Typeface.NORMAL)
                            //Creamos otro texto
                            val textDescEncuesta = TextView(this@EmpresaActivity)
                            textDescEncuesta.text = "Dirección: "+arrayItem.strDireccion
                            textDescEncuesta.textSize = 14f
                            textDescEncuesta.setTypeface(Typeface.MONOSPACE, Typeface.ITALIC)
                            textDescEncuesta.setTextColor(Color.parseColor("#E0F2F1"))
                            cardLinearLayout.addView(textEncuesta)
                            cardLinearLayout.addView(textDescEncuesta)
                            cardView.setOnClickListener {
                                getViewEncuesta(arrayItem.intIdEmpresa)
                            }
                            cardView.addView(cardLinearLayout)
                            //Agregamos el cardView a nuestro layout
                            empresaLayoutSecundario.addView(cardView)
                            Log.d("Interceptor","----------------------")
                        }
                    }
                    val buttonCerrarSesion = Button(this@EmpresaActivity)
                    buttonCerrarSesion.setLayoutParams(buttonLayout)
                    buttonCerrarSesion.setText("Cerrar Sesión")
                    buttonCerrarSesion.setBackgroundColor(Color.BLUE)
                    buttonCerrarSesion.setTextColor(Color.WHITE)
                    buttonCerrarSesion.setOnClickListener {
                        Toast.makeText(
                            this@EmpresaActivity,
                            "hasta luego",
                            Toast.LENGTH_LONG
                        ).show()
                        //Borrado de datos
                        val prefs = getSharedPreferences(getString(R.string.prefs_file),Context.MODE_PRIVATE).edit()
                        prefs.clear()
                        prefs.apply()
                        //Cerrar session
                        FirebaseAuth.getInstance().signOut()
                        onBackPressed()//volver a la pantalla anterior

                    }
                    empresaLayoutFooter.addView(buttonCerrarSesion)
                } else {
                    Toast.makeText(this@EmpresaActivity,"Error al consumir Web Services getEmpresa", Toast.LENGTH_LONG).show()
                }
            }catch (Ex:Exception){
                Log.e("Error",Ex.localizedMessage)
            }
        }
        /*
        artefactaTestButton.setOnClickListener {
            val encuestaActivityIntent = Intent(this, EncuestaActivity::class.java).apply()
            {
            }
            startActivity(encuestaActivityIntent)
            /*val servicesListIntent = Intent(this, ServiciosActivity::class.java).apply()
            {
            }
            startActivity(servicesListIntent)*/
        }*/
    }
    private fun getViewEncuesta(idEmpresa: Int)
    {
        val encuestaActivityIntent = Intent(this, EncuestaActivity::class.java).apply()
        {
            putExtra("idEmpresa",idEmpresa)
        }
        startActivity(encuestaActivityIntent)
    }
}