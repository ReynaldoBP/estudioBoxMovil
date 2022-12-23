package com.massvision.estudiobox.View

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.JsonObject
import com.massvision.estudiobox.R
import com.massvision.estudiobox.Repository.ApiService
import com.massvision.estudiobox.Repository.RetrofitHelper
import kotlinx.android.synthetic.main.activity_empresa.*
import kotlinx.android.synthetic.main.activity_login.*

class EmpresaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_empresa)
        val bundle= intent.extras
        val email = bundle?.getString("email")
        Log.d("Interceptor","Empresa activity: "+email)
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email",email)
        prefs.apply()
        if(email!=null)
        {
            setup(email)
        }
    }
    private fun setup(email:String)
    {
        title = "Empresas Participantes"
        val pDialog = SweetAlertDialog(this@EmpresaActivity, SweetAlertDialog.PROGRESS_TYPE)
        pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
        pDialog.titleText = "Cargando ..."
        pDialog.setCancelable(true)
        pDialog.show()
        //consumo ApiRest
        Log.d("Interceptor","consumo ApiRest")
        val apiService = RetrofitHelper.getInstance().create(ApiService::class.java)
        val jsonObject = JsonObject()
        lifecycleScope.launchWhenCreated {
            try {
                val response = apiService.getEmpresa(jsonObject)
                if (response.isSuccessful()) {
                    pDialog.hide()
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
                        val spaceLayout = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, 16
                        )
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
                            cardView.radius = 25f
                            val color = ContextCompat.getColor(this@EmpresaActivity, com.google.android.material.R.color.design_default_color_primary_variant)
                            cardView.setCardBackgroundColor(color)
                            cardView.setContentPadding(36,36,36,36)
                            cardView.layoutParams = params
                            //cardView.cardElevation = 30f
                            //Creamos el texto para la empresa
                            val textEmpresa = TextView(this@EmpresaActivity)
                            textEmpresa.text = "Empresa: "+arrayItem.strNombreComercial;
                            textEmpresa.textSize = 20f
                            textEmpresa.setTextColor(Color.WHITE)
                            textEmpresa.setTypeface(Typeface.SANS_SERIF, Typeface.NORMAL)
                            //Creamos otro texto para el detalle de la empresa
                            val textDescEmpresa = TextView(this@EmpresaActivity)
                            textDescEmpresa.text = "Dirección: "+arrayItem.strDireccion
                            textDescEmpresa.textSize = 17f
                            textDescEmpresa.setTypeface(Typeface.MONOSPACE, Typeface.ITALIC)
                            textDescEmpresa.setTextColor(Color.WHITE)
                            val space = Space(this@EmpresaActivity)
                            space.setLayoutParams(spaceLayout)

                            //Creamos otro texto para ver las encuestas
                            val textVerEncuesta = TextView(this@EmpresaActivity)
                            textVerEncuesta.text = "Ver Encuesta >>"
                            textVerEncuesta.textSize = 14f
                            textVerEncuesta.setTypeface(Typeface.MONOSPACE, Typeface.ITALIC)
                            textVerEncuesta.setTextColor(Color.WHITE)
                            //textVerEncuesta.setGravity(Gravity.CENTER_VERTICAL or Gravity.CENTER_HORIZONTAL)
                            cardLinearLayout.addView(textEmpresa)
                            cardLinearLayout.addView(textDescEmpresa)
                            cardLinearLayout.addView(space)
                            cardLinearLayout.addView(textVerEncuesta)
                            cardView.setOnClickListener {
                                getViewEncuesta(arrayItem.intIdEmpresa,email)
                            }
                            cardView.addView(cardLinearLayout)
                            //Agregamos el cardView a nuestro layout
                            empresaLayoutSecundario.addView(cardView)
                            Log.d("Interceptor","----------------------")
                        }
                    }
                    buttonCerrarSesion.setOnClickListener {
                        SweetAlertDialog(this@EmpresaActivity, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("¿Estás seguro de que quieres cerrar sesión?")
                            //.setContentText("You won't be able to recover this file!")
                            .setConfirmText("Aceptar")
                            .setConfirmClickListener { sDialog -> sDialog.dismissWithAnimation()
                                val prefs = getSharedPreferences(getString(R.string.prefs_file),Context.MODE_PRIVATE).edit()
                                prefs.clear()
                                prefs.apply()
                                //Cerrar session
                                FirebaseAuth.getInstance().signOut()
                                onBackPressed()//volver a la pantalla anterior
                            }
                            .setCancelButton(
                                "Cancelar"
                            ) { sDialog -> sDialog.dismissWithAnimation() }
                            .show()
                        //Borrado de datos
                    }
                } else {
                    pDialog.hide()
                    SweetAlertDialog(this@EmpresaActivity, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Error")
                        .setContentText("Ha ocurrido un error, por favor inténtalo de nuevo más tarde")
                        .show()
                }
            }catch (Ex:Exception){
                pDialog.hide()
                SweetAlertDialog(this@EmpresaActivity, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error")
                    .setContentText("Ha ocurrido un error, por favor inténtalo de nuevo más tarde")
                    .show()
                Log.e("Interceptor",Ex.localizedMessage)
            }
        }
    }
    private fun getViewEncuesta(idEmpresa: Int,email: String)
    {
        val encuestaActivityIntent = Intent(this, EncuestaActivity::class.java).apply()
        {
            putExtra("idEmpresa",idEmpresa)
            putExtra("email",email)
        }
        startActivity(encuestaActivityIntent)
    }
}