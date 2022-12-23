package com.massvision.estudiobox.View

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.gson.JsonObject
import com.massvision.estudiobox.R
import com.massvision.estudiobox.Repository.ApiService
import com.massvision.estudiobox.Repository.RetrofitHelper
import kotlinx.android.synthetic.main.activity_crear_cuenta.*
import kotlinx.android.synthetic.main.activity_login.*
import java.util.regex.Matcher
import java.util.regex.Pattern

class CrearCuentaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_cuenta)
        setup()
    }
    private fun setup()
    {
        uneteTextView.textSize = 20f
        uneteTextView.setTextColor(Color.BLACK)
        uneteTextView.setTypeface(Typeface.SANS_SERIF, Typeface.NORMAL)
        val jsonData = JsonObject()
        val generalLayout = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        val arrayGenero = ArrayList<String>()
        arrayGenero.add("Seleccione su Género")
        arrayGenero.add("Masculino")
        arrayGenero.add("Femenino")
        arrayGenero.add("Otros")
        val spinnerGenero = findViewById<Spinner>(R.id.generoSpinner)
        val spinnerArrayAdapter = ArrayAdapter(
            this@CrearCuentaActivity,
            android.R.layout.simple_spinner_dropdown_item,
            arrayGenero
        )
        spinnerGenero.adapter = spinnerArrayAdapter
        spinnerGenero.setGravity(Gravity.CENTER_VERTICAL or Gravity.CENTER_HORIZONTAL)
        spinnerGenero.setLayoutParams(generalLayout)
        regresarButton.setOnClickListener {
            val loginActivityListIntent = Intent(this, LoginActivity::class.java).apply()
            {
            }
            startActivity(loginActivityListIntent)
        }
        spinnerGenero.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                Toast.makeText(
                    this@CrearCuentaActivity,
                    "onNothingSelected",
                    Toast.LENGTH_LONG
                ).show()
            }
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                jsonData.addProperty(
                    "strGenero",
                    arrayGenero[position].toString()
                )
            }
        }
        //Fecha de nacimiento del encuestado
        /*val textFechaNac = findViewById<EditText>(R.id.fechaNacimientoEditText)
        textFechaNac.setHint("Ingrese su Fecha de Nacimiento")
        textFechaNac.isClickable=true
        textFechaNac.isFocusable=false
        textFechaNac.setOnClickListener{
            val datePicker = DatePickerFragment { day, month, year -> textFechaNac.setText("$year-$month-$day") }
            datePicker.show(supportFragmentManager, "datePicker")
        }
        textFechaNac.setLayoutParams(generalLayout)
        textFechaNac.addTextChangedListener {
            jsonData.addProperty(
                "strEdad",
                textFechaNac.getText().toString()
            )
        }*/
        registrarButton.setOnClickListener {
            try {
                val pDialog = SweetAlertDialog(this@CrearCuentaActivity, SweetAlertDialog.PROGRESS_TYPE)
                pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
                pDialog.titleText = "Cargando ..."
                pDialog.setCancelable(true)
                pDialog.show()
                if(identificacionEditTextNumber.text.isNotEmpty())
                {
                    if(identificacionEditTextNumber.text.length > 9 && identificacionEditTextNumber.text.length < 14)
                    {
                        jsonData.addProperty("strIdentificacion", identificacionEditTextNumber.text.toString())
                    }
                    else{
                        pDialog.hide()
                        throw Exception ("Identificación ingresada no es válida")
                    }
                }
                if (correoEditText.text.isEmpty() || contraseñaEditText.text.isEmpty()) {
                    pDialog.hide()
                    throw Exception ("Por favor ingresa al menos tu correo electrónico y contraseña, para crear la cuenta")
                }
                if(validarCorreo(correoEditText.text.toString()) == false) {
                    pDialog.hide()
                    throw Exception ("Correo electrónico ingresado no es válido")
                }
                val apiService = RetrofitHelper.getInstance().create(ApiService::class.java)
                jsonData.addProperty("strNombre", nombresCompletosEditText.text.toString())
                jsonData.addProperty("strCorreo", correoEditText.text.toString())
                jsonData.addProperty("strContrasenia", contraseñaEditText.text.toString())
                jsonData.addProperty("strAutenticacionRs", "N")
                jsonData.addProperty("strEstado", "Activo")
                jsonData.addProperty("strUsrSesion", "appMovil")
                val jsonObject = JsonObject()
                jsonObject.add("data", jsonData)
                Log.d("Interceptor", "Request: " + jsonObject)

                lifecycleScope.launchWhenCreated {
                    try {
                        val response = apiService.createCliente(jsonObject)
                        if (response.isSuccessful()) {
                            identificacionEditTextNumber.setText("")
                            nombresCompletosEditText.setText("")
                            correoEditText.setText("")
                            contraseñaEditText.setText("")
                            fechaNacimientoEditTextDate.setText("")
                            pDialog.hide()
                            Log.d("Interceptor", "resultado del createCliente isSuccessful")
                            Log.d("Interceptor", "Response: " + response.body().toString())
                            SweetAlertDialog(this@CrearCuentaActivity, SweetAlertDialog.SUCCESS_TYPE)
                                //.setTitleText("Are you sure?")
                                .setContentText(response.body()?.strMensaje.toString())
                                .setConfirmText("Aceptar")
                                .setConfirmClickListener {
                                    finish();
                                    startActivity(getIntent());
                                }
                                .show()
                            /*SweetAlertDialog(this@CrearCuentaActivity)
                                .setTitleText(response.body()?.strMensaje.toString())
                                .show();*/
                        } else {
                            pDialog.hide()
                            SweetAlertDialog(this@CrearCuentaActivity, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Error")
                                .setContentText("Ha ocurrido un error, por favor inténtalo de nuevo más tarde")
                                .show()
                        }
                    } catch (Ex: Exception) {
                        pDialog.hide()
                        SweetAlertDialog(this@CrearCuentaActivity, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error")
                            .setContentText("Ha ocurrido un error, por favor inténtalo de nuevo más tarde")
                            .show()
                        Log.e("Interceptor", Ex.localizedMessage)
                    }
                }
            } catch (Ex: Exception) {
                SweetAlertDialog(this@CrearCuentaActivity, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error")
                    .setContentText(Ex.localizedMessage)
                    .show()
                Log.e("Interceptor", Ex.localizedMessage)
            }
        }
    }
    private fun validarCorreo(texto:String):Boolean{
        var patroncito:Pattern=Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")
        var comparador:Matcher=patroncito.matcher(texto)
        return comparador.find()
    }
}