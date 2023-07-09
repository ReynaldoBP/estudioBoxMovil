package com.massvision.estudiobox.View

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.os.Bundle
import android.os.CountDownTimer
import android.text.InputType
import android.text.method.DigitsKeyListener
import android.util.Log
import android.view.Gravity
import android.view.Gravity.CENTER
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.gson.JsonObject
import com.massvision.estudiobox.R
import com.massvision.estudiobox.Repository.ApiService
import com.massvision.estudiobox.Repository.RetrofitHelper
import kotlinx.android.synthetic.main.activity_pregunta.*

private val INACTIVITY_TIMEOUT: Long = 5 * 60 * 1000 // 5 minutos de inactividad
//private val INACTIVITY_TIMEOUT: Long = 1 * 60 * 100 // 6 segundos de inactividad
private var inactivityTimer: CountDownTimer? = null
class PreguntaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pregunta)
        val bundle= intent.extras
        //setup(email?:"",provider?:"")
        val idEncuesta = bundle?.getInt("idEncuesta")
        val email = bundle?.getString("email")
        if (idEncuesta != null && email!= null) {
            setup(idEncuesta,email)
        }
        Log.d("Interceptor","idEncuesta: "+idEncuesta+" email:"+email)

        // Inicializar el temporizador de inactividad
        inactivityTimer = object : CountDownTimer(INACTIVITY_TIMEOUT, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                // Nada que hacer en cada tick
            }
            override fun onFinish() {
                // Acción a ejecutar cuando el temporizador llega a cero
                Log.d("Interceptor","---Inactividad---")
                getViewPublicidad()
            }
        }.start()
    }
    override fun onUserInteraction() {
        // Reiniciar el temporizador de inactividad cada vez que se detecte una interacción del usuario
        Log.d("Interceptor","+++Actividad+++")
        inactivityTimer?.cancel()
        inactivityTimer?.start()
    }
    private fun setup(idEncuesta:Int,email:String)
    {
        title = "Preguntas"
        Log.d("Interceptor", "idEncuesta: "+idEncuesta)
        val pDialog = SweetAlertDialog(this@PreguntaActivity, SweetAlertDialog.PROGRESS_TYPE)
        pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
        pDialog.titleText = "Cargando ..."
        pDialog.setCancelable(true)
        pDialog.show()
        //consumo ApiRest
        Log.d("Interceptor","consumo ApiRest")
        val apiService = RetrofitHelper.getInstance().create(ApiService::class.java)
        val jsonData = JsonObject()
        jsonData.addProperty("intIdEncuesta",idEncuesta)
        jsonData.addProperty("strUsrSesion",email)
        val jsonObject = JsonObject()
        jsonObject.add("data",jsonData)
        lifecycleScope.launchWhenCreated {
            try {
                val response = apiService.getPregunta(jsonObject)
                if (response.isSuccessful()) {
                    pDialog.hide()
                    Log.d("Interceptor", "resultado del getPregunta isSuccessful")
                    Log.d("Interceptor", "Response: " + response.body().toString())
                    //Establecemos el layout
                    setContentView(R.layout.activity_pregunta)
                    //Obtenemos el linear layout donde colocar los botones
                    val preguntaLayoutSecundario =
                        findViewById<LinearLayout>(R.id.preguntaLayoutSecundario)
                    //Creamos las propiedades de layout que tendrán los botones.
                    val generalLayout = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    //Creamos las propiedades de layout que tendrán los campos desplegables.
                    val desplegableLayout = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    desplegableLayout.gravity = Gravity.CENTER_HORIZONTAL
                    //desplegableLayout.gravity = Gravity.LEFT
                    val spaceLayout = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, 16
                    )
                    //Parametros CardView
                    val parametrosCardView = RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT)
                    parametrosCardView.setMargins(16,16,16,16)
                    val cardViewPreguntas = CardView(this@PreguntaActivity)
                    cardViewPreguntas.radius = 15f
                    cardViewPreguntas.radius = 25f
                    val color = ContextCompat.getColor(this@PreguntaActivity, android.R.color.white)
                    cardViewPreguntas.setCardBackgroundColor(color)
                    cardViewPreguntas.setContentPadding(36,36,36,36)
                    cardViewPreguntas.layoutParams = parametrosCardView
                    //Creamos el layaut que va a contener el card view
                    val cardLinearLayoutPreguntas = LinearLayout(this@PreguntaActivity)
                    cardLinearLayoutPreguntas.orientation = LinearLayout.VERTICAL
                    if (response.body()?.intStatus == 200 && response.body()?.arrayPregunta?.isNotEmpty() == true) {
                        Log.d("Interceptor", "+++++++++++++++")
                        val jsonPregunta = JsonObject()
                        //Encabezado de la encuesta
                        val textEncuesta = TextView(this@PreguntaActivity)
                        textEncuesta.textSize = 25f
                        textEncuesta.setGravity(Gravity.CENTER_VERTICAL or Gravity.CENTER_HORIZONTAL)
                        textEncuesta.setLayoutParams(generalLayout)
                        textEncuesta.setText("Encuesta")
                        textEncuesta.setBackgroundColor(Color.TRANSPARENT)
                        textEncuesta.setTextColor(Color.BLACK)
                        textEncuesta.setTypeface(null, Typeface.BOLD)// Establecer estilo negrita
                        cardLinearLayoutPreguntas.addView(textEncuesta)
                        //Recorremos las preguntas
                        for (arrayItem in response.body()?.arrayPregunta!!) {
                            Log.d("Interceptor", "Pregunta: " + arrayItem.strPregunta)
                            //Presentamos las preguntas
                            val textPregunta = TextView(this@PreguntaActivity)
                            textPregunta.textSize = 17f
                            //textPregunta.setGravity(Gravity.CENTER_VERTICAL or Gravity.CENTER_HORIZONTAL)
                            textPregunta.gravity = Gravity.LEFT
                            textPregunta.setLayoutParams(generalLayout)
                            textPregunta.setText(arrayItem.strPregunta)
                            textPregunta.setBackgroundColor(Color.TRANSPARENT)
                            textPregunta.setTextColor(Color.BLACK)
                            textPregunta.setTypeface(null, Typeface.BOLD)// Establecer estilo negrita
                            cardLinearLayoutPreguntas.addView(textPregunta)
                            //Presentamos las estrellas
                            if (arrayItem.strTipoOpcionRespuesta == "CERRADA") {
                                if (arrayItem.intCantidadEstrellas == 5){
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
                                    jsonPregunta.addProperty(ratingBar.id.toString(), "")
                                    ratingBar.onRatingBarChangeListener =
                                        RatingBar.OnRatingBarChangeListener { _, rating, _ ->
                                            jsonPregunta.addProperty(
                                                ratingBar.id.toString(),
                                                rating.toInt()
                                            )
                                        }
                                    //ratingBar.progressBackgroundTintList(Color.BLUE)
                                    cardLinearLayoutPreguntas.addView(ratingBar)
                                }
                                if (arrayItem.intCantidadEstrellas == 10){
                                    val respIpnButton = Button(this@PreguntaActivity)
                                    respIpnButton.setLayoutParams(generalLayout)
                                    respIpnButton.setText("0")
                                    respIpnButton.setBackgroundColor(Color.TRANSPARENT)
                                    respIpnButton.setTextColor(Color.BLACK)
                                    cardLinearLayoutPreguntas.addView(respIpnButton)
                                    val ipnSeekBar = SeekBar(this@PreguntaActivity)
                                    ipnSeekBar.max = 10
                                    ipnSeekBar.id= arrayItem.intIdPregunta
                                    val thumb = ShapeDrawable(OvalShape())
                                    thumb.intrinsicHeight = 80
                                    thumb.intrinsicWidth = 30
                                    ipnSeekBar.thumb = thumb
                                    ipnSeekBar.progress = 0
                                    ipnSeekBar.visibility = View.VISIBLE
                                    ipnSeekBar.setLayoutParams(generalLayout)
                                    var startPoint = 0
                                    var endPoint = 10
                                    jsonPregunta.addProperty(ipnSeekBar.id.toString(), "0")
                                    ipnSeekBar.setOnSeekBarChangeListener(object :
                                        OnSeekBarChangeListener {
                                        override fun onStopTrackingTouch(arg0: SeekBar) {
                                            if (arg0 != null) {
                                                endPoint = arg0.progress
                                            }
                                        }
                                        override fun onStartTrackingTouch(arg0: SeekBar) {
                                            if (arg0 != null) {
                                                startPoint = arg0.progress
                                            }
                                        }
                                        override fun onProgressChanged(
                                            arg0: SeekBar,
                                            arg1: Int,
                                            arg2: Boolean
                                        ) {
                                            respIpnButton.setText(arg1.toString())
                                            jsonPregunta.addProperty(
                                                ipnSeekBar.id.toString(),
                                                arg1.toString()
                                            )
                                        }
                                    })
                                    cardLinearLayoutPreguntas.addView(ipnSeekBar)
                                }
                            }
                            else if(arrayItem.strTipoOpcionRespuesta == "DESPLEGABLE") {
                                if(!arrayItem.strValorDesplegable.isEmpty())
                                {
                                    val arrayValorDesplegable = ArrayList(arrayItem.strValorDesplegable.split("|"))
                                    val spinnerDesplegable = Spinner(this@PreguntaActivity)
                                    val spinnerArrayAdapter = ArrayAdapter(
                                        this@PreguntaActivity,
                                        android.R.layout.simple_spinner_dropdown_item,
                                        arrayValorDesplegable
                                    )
                                    spinnerDesplegable.adapter = spinnerArrayAdapter
                                    spinnerDesplegable.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                                        //((TextView) adapterView.getChildAt(0)).setGravity(Gravity.CENTER)
                                        override fun onNothingSelected(parent: AdapterView<*>?) {
                                            Toast.makeText(
                                                this@PreguntaActivity,
                                                "onNothingSelected",
                                                Toast.LENGTH_LONG,
                                            ).show()
                                        }
                                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                            jsonPregunta.addProperty(
                                                arrayItem.intIdPregunta.toString(),
                                                arrayValorDesplegable[position].toString()
                                            )
                                        }
                                    }
                                    //spinnerDesplegable.setGravity(Gravity.CENTER_VERTICAL or Gravity.CENTER_HORIZONTAL)
                                    spinnerDesplegable.setGravity(Gravity.LEFT)
                                    spinnerDesplegable.setLayoutParams(desplegableLayout)
                                    cardLinearLayoutPreguntas.addView(spinnerDesplegable)
                                }
                            }
                            else if(arrayItem.strTipoOpcionRespuesta == "CAJA") {
                                if(!arrayItem.strValorDesplegable.isEmpty())
                                {
                                    val arrayValorDesplegable = ArrayList(arrayItem.strValorDesplegable.split("|"))
                                    val gridCheckboxLayout = GridLayout(this@PreguntaActivity)
                                    gridCheckboxLayout.columnCount = 2
                                    val checkBoxGroup = ArrayList<CheckBox>()
                                    var selectedValue: String? = null
                                    //Recorremos todas las opciones de respuesta
                                    for (arrayItemValorDesplegable in arrayValorDesplegable) {
                                        //creamos los campos de tipo CheckBox
                                        val checkBoxDesplegable = CheckBox(this@PreguntaActivity)
                                        checkBoxDesplegable.text = arrayItemValorDesplegable
                                        checkBoxDesplegable.layoutParams = desplegableLayout
                                        checkBoxDesplegable.layoutParams = GridLayout.LayoutParams().apply {
                                            columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                                        }
                                        checkBoxDesplegable.setOnCheckedChangeListener { buttonView, isChecked ->
                                            if (isChecked) {
                                                // Deseleccionar los demás CheckBox
                                                for (cb in checkBoxGroup) {
                                                    if (cb != buttonView) {
                                                        cb.isChecked = false
                                                    }
                                                }
                                                //Guardar los valores
                                                selectedValue = arrayItemValorDesplegable
                                                jsonPregunta.addProperty(
                                                    arrayItem.intIdPregunta.toString(),
                                                    selectedValue)
                                            }
                                            else
                                            {
                                                selectedValue = null
                                                //Quitar los valores
                                                jsonPregunta.addProperty(
                                                    arrayItem.intIdPregunta.toString(),
                                                    "")
                                            }
                                        }
                                        checkBoxGroup.add(checkBoxDesplegable)
                                        gridCheckboxLayout.addView(checkBoxDesplegable)
                                    }
                                    cardLinearLayoutPreguntas.addView(gridCheckboxLayout)
                                }
                            }
                            //Presentamos texto para las preguntas con comentarios
                            else
                            {
                                val text = EditText(this@PreguntaActivity)
                                text.id = arrayItem.intIdPregunta
                                //text.setGravity(Gravity.CENTER_VERTICAL or Gravity.CENTER_HORIZONTAL)
                                text.setGravity(Gravity.LEFT)
                                text.setLayoutParams(generalLayout)
                                jsonPregunta.addProperty(text.id.toString(),"")
                                text.addTextChangedListener {
                                    jsonPregunta.addProperty(
                                        text.id.toString(),
                                        text.getText().toString()
                                    )
                                }
                                cardLinearLayoutPreguntas.addView(text)
                            }
                            val space = Space(this@PreguntaActivity)
                            space.setLayoutParams(spaceLayout)
                            cardLinearLayoutPreguntas.addView(space)
                            Log.d("Interceptor", "----------------------")
                        }

                        val jsonDataRespuesta = JsonObject()
                        jsonDataRespuesta.addProperty("intIdEncuesta",idEncuesta)
                        jsonDataRespuesta.add("arrayPregunta",jsonPregunta)
                        //Datos Adicionales
                        //Creamos el cardView de Datos Adicionales
                        val cardViewDA = CardView(this@PreguntaActivity)
                        cardViewDA.radius = 15f
                        cardViewDA.radius = 25f
                        val color = ContextCompat.getColor(this@PreguntaActivity, android.R.color.white)
                        cardViewDA.setCardBackgroundColor(color)
                        cardViewDA.setContentPadding(36,36,36,36)
                        cardViewDA.layoutParams = parametrosCardView
                        //Creamos el layaut que va a contener el card view
                        val cardLinearLayoutDA = LinearLayout(this@PreguntaActivity)
                        cardLinearLayoutDA.orientation = LinearLayout.VERTICAL
                        //Establecemos espacio
                        val space = Space(this@PreguntaActivity)
                        space.setLayoutParams(spaceLayout)
                        cardLinearLayoutDA.addView(space)

                        val textDatosAdicionales = TextView(this@PreguntaActivity)
                        textDatosAdicionales.textSize = 25f
                        textDatosAdicionales.setGravity(Gravity.CENTER_VERTICAL or Gravity.CENTER_HORIZONTAL)
                        textDatosAdicionales.setLayoutParams(generalLayout)
                        textDatosAdicionales.setText("Datos Adicionales")
                        textDatosAdicionales.setBackgroundColor(Color.TRANSPARENT)
                        textDatosAdicionales.setTextColor(Color.BLACK)
                        textDatosAdicionales.setTypeface(null, Typeface.BOLD)// Establecer estilo negrita
                        cardLinearLayoutDA.addView(textDatosAdicionales)
                        //Correo electronico del encuestado
                        val textCorreo = EditText(this@PreguntaActivity)
                        textCorreo.setInputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
                        textCorreo.setHint("Ingrese su Correo Electrónico")
                        textCorreo.addTextChangedListener {
                            jsonDataRespuesta.addProperty(
                                "strCorreo",
                                textCorreo.getText().toString()
                            )
                            jsonDataRespuesta.addProperty(
                                "strNombre",
                                textCorreo.getText().toString().split("@")[0]
                            )
                        }
                        //textCorreo.setGravity(Gravity.CENTER_VERTICAL or Gravity.CENTER_HORIZONTAL)
                        textCorreo.setGravity(Gravity.LEFT)
                        textCorreo.setLayoutParams(generalLayout)
                        cardLinearLayoutDA.addView(textCorreo)
                        //Fecha de nacimiento del encuestado
                        val textFechaNac = EditText(this@PreguntaActivity)
                        textFechaNac.setInputType(InputType.TYPE_DATETIME_VARIATION_DATE)
                        textFechaNac.setHint("Ingrese su Año de Nacimiento")
                        textFechaNac.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
                        /*textFechaNac.isClickable=true
                        textFechaNac.isFocusable=false
                        textFechaNac.setOnClickListener{
                            val datePicker = DatePickerFragment { day, month, year -> textFechaNac.setText("$year-$month-$day") }
                            datePicker.show(supportFragmentManager, "datePicker")
                        }*/
                        textFechaNac.addTextChangedListener {
                            jsonDataRespuesta.addProperty(
                                "strEdad",
                                textFechaNac.getText().toString()
                            )
                        }
                        //textFechaNac.setGravity(Gravity.CENTER_VERTICAL or Gravity.CENTER_HORIZONTAL)
                        textFechaNac.setGravity(Gravity.LEFT)

                        textFechaNac.setLayoutParams(generalLayout)
                        cardLinearLayoutDA.addView(textFechaNac)
                        //ComboBox Genero del encuestado
                        val arrayGenero = ArrayList<String>()
                        arrayGenero.add("Seleccione su Género")
                        arrayGenero.add("Masculino")
                        arrayGenero.add("Femenino")
                        arrayGenero.add("Otros")
                        val spinnerGenero = Spinner(this@PreguntaActivity)
                        val spinnerArrayAdapter = ArrayAdapter(
                            this@PreguntaActivity,
                            android.R.layout.simple_spinner_dropdown_item,
                            arrayGenero
                        )
                        spinnerGenero.adapter = spinnerArrayAdapter
                        spinnerGenero.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            //((TextView) adapterView.getChildAt(0)).setGravity(Gravity.CENTER)
                            override fun onNothingSelected(parent: AdapterView<*>?) {
                                Toast.makeText(
                                    this@PreguntaActivity,
                                    "onNothingSelected",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                jsonDataRespuesta.addProperty(
                                    "strGenero",
                                    arrayGenero[position].toString()
                                )
                            }
                        }
                        spinnerGenero.setGravity(Gravity.CENTER_VERTICAL or Gravity.CENTER_HORIZONTAL)
                        spinnerGenero.setLayoutParams(desplegableLayout)
                        // Crear text de genero
                        val textGenero = TextView(this@PreguntaActivity)
                        textGenero.textSize = 17f
                        textGenero.setGravity(Gravity.CENTER_VERTICAL or Gravity.CENTER_HORIZONTAL)
                        textGenero.setLayoutParams(generalLayout)
                        textGenero.setText("Seleccione su Género")
                        textGenero.setBackgroundColor(Color.TRANSPARENT)
                        textGenero.setTextColor(Color.BLACK)
                        textGenero.setTypeface(null, Typeface.BOLD)// Establecer estilo negrita
                        cardLinearLayoutDA.addView(textGenero)
                        // Crear CheckBox de genero
                        val checkBoxGeneroMasc = CheckBox(this@PreguntaActivity)
                        checkBoxGeneroMasc.text = "Masculino"
                        checkBoxGeneroMasc.layoutParams = desplegableLayout
                        val checkBoxGeneroFem = CheckBox(this@PreguntaActivity)
                        checkBoxGeneroFem.text = "Femenino"
                        checkBoxGeneroFem.layoutParams = desplegableLayout
                        jsonDataRespuesta.addProperty(
                            "strGenero",""
                        )
                        // Obtener el valor del CheckBox seleccionado Masculino
                        checkBoxGeneroMasc.setOnCheckedChangeListener { buttonView, isChecked ->
                            if (isChecked) {
                                // Checkbox Masculino seleccionado
                                checkBoxGeneroFem.isChecked = false // Deseleccionar Checkbox Femenino
                                jsonDataRespuesta.addProperty(
                                    "strGenero","Masculino"
                                )
                            } else {
                                // Checkbox Masculino no seleccionado
                                jsonDataRespuesta.addProperty(
                                    "strGenero",""
                                )
                            }
                        }
                        // Obtener el valor del CheckBox seleccionado Femenino
                        checkBoxGeneroFem.setOnCheckedChangeListener { buttonView, isChecked ->
                            if (isChecked) {
                                // Checkbox Femenino seleccionado
                                checkBoxGeneroMasc.isChecked = false // Deseleccionar Checkbox Femenino
                                jsonDataRespuesta.addProperty(
                                    "strGenero","Femenino"
                                )
                            } else {
                                // Checkbox Femenino no seleccionado
                                jsonDataRespuesta.addProperty(
                                    "strGenero",""
                                )
                            }
                        }
                        cardLinearLayoutDA.addView(checkBoxGeneroMasc)
                        cardLinearLayoutDA.addView(checkBoxGeneroFem)
                        //cardLinearLayoutDA.addView(spinnerGenero)
                        cardViewDA.addView(cardLinearLayoutDA)
                        cardViewPreguntas.addView(cardLinearLayoutPreguntas)
                        preguntaLayoutSecundario.addView(cardViewPreguntas)
                        preguntaLayoutSecundario.addView(cardViewDA)

                        jsonDataRespuesta.addProperty("strUsrSesion", email)
                        //Guardar Encuesta
                        buttonGuardarEncuesta.setOnClickListener {
                            val jsonObjectRespuesta = JsonObject()
                            jsonObjectRespuesta.add("data",jsonDataRespuesta)
                            createRespuesta(jsonObjectRespuesta)
                        }
                    }
                    else if(response.body()?.intStatus==204)
                    {
                        SweetAlertDialog(this@PreguntaActivity, SweetAlertDialog.WARNING_TYPE)
                            //.setTitleText("Are you sure?")
                            .setContentText(response.body()?.strMensaje.toString())
                            .setConfirmText("Aceptar")
                            .setConfirmClickListener {
                                getViewEncuesta(email)
                            }.show()
                    }
                    else {
                        SweetAlertDialog(this@PreguntaActivity, SweetAlertDialog.WARNING_TYPE)
                            //.setTitleText("Are you sure?")
                            .setContentText(response.body()?.strMensaje.toString())
                            .setConfirmText("Aceptar")
                            .setConfirmClickListener {
                                getViewEncuesta(email)
                            }.show()
                    }
                }
                else {
                    pDialog.hide()
                    SweetAlertDialog(this@PreguntaActivity, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Error")
                        .setContentText("Ha ocurrido un error, por favor inténtalo de nuevo más tarde")
                        .show()
                }
            } catch (Ex: Exception) {
                pDialog.hide()
                SweetAlertDialog(this@PreguntaActivity, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error")
                    .setContentText(Ex.localizedMessage)
                    .show()
                Log.e("Interceptor",Ex.localizedMessage)

            }
        }
    }
    private fun createRespuesta(jsonObject:JsonObject)
    {
        try {
            Log.d("Interceptor", "Request Respuesta: " + jsonObject)
            val pDialog = SweetAlertDialog(this@PreguntaActivity, SweetAlertDialog.PROGRESS_TYPE)
            pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
            pDialog.titleText = "Cargando ..."
            pDialog.setCancelable(true)
            pDialog.show()
            val apiService = RetrofitHelper.getInstance().create(ApiService::class.java)
            lifecycleScope.launchWhenCreated {
                try {
                    val response = apiService.createRespuesta(jsonObject)
                    Log.d("Interceptor","resultado del createRespuesta isSuccessful")
                    Log.d("Interceptor","Response: "+response.body().toString())
                    if (response.isSuccessful()) {
                        pDialog.hide()
                        if(response.body()?.intStatus==200)
                        {
                            SweetAlertDialog(this@PreguntaActivity, SweetAlertDialog.SUCCESS_TYPE)
                                //.setTitleText("Are you sure?")
                                .setContentText(response.body()?.strMensaje.toString())
                                .setConfirmText("Aceptar")
                                .setConfirmClickListener {
                                    finish();
                                    startActivity(getIntent());
                                }
                                .show()
                            /*SweetAlertDialog(this@PreguntaActivity)
                                .setTitleText(response.body()?.strMensaje.toString())
                                .show();*/
                        }
                        else if(response.body()?.intStatus==204)
                        {
                            SweetAlertDialog(this@PreguntaActivity, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Error")
                                .setContentText(response.body()?.strMensaje)
                                .show()
                        }
                        else{
                            SweetAlertDialog(this@PreguntaActivity, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Error")
                                .setContentText("Ha ocurrido un error, por favor inténtalo de nuevo más tarde")
                                .show()
                        }
                    } else {
                        pDialog.hide()
                        SweetAlertDialog(this@PreguntaActivity, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error")
                            .setContentText("Ha ocurrido un error, por favor inténtalo de nuevo más tarde")
                            .show()
                    }
                }catch (Ex:Exception){
                    pDialog.hide()
                    SweetAlertDialog(this@PreguntaActivity, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Error")
                        .setContentText("Ha ocurrido un error, por favor inténtalo de nuevo más tarde")
                        .show()
                    Log.e("Interceptor",Ex.localizedMessage)
                }
            }
        }catch (Ex:Exception){
            SweetAlertDialog(this@PreguntaActivity, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Error")
                .setContentText(Ex.localizedMessage)
                .show()
        }
    }
    private fun getViewEncuesta(email:String)
    {
        val encuestaActivityIntent = Intent(this, EncuestaActivity::class.java).apply()
        {
            putExtra("email",email)
        }
        startActivity(encuestaActivityIntent)
    }
    private fun getViewPublicidad()
    {
        val am = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val tasks = am.getRunningTasks(Int.MAX_VALUE)
        if (tasks.isNotEmpty()) {
            val topActivity = tasks[0].topActivity
            if (topActivity != null) {
                if (topActivity.packageName == packageName && topActivity.className == PublicidadActivity::class.java.name) {
                    Log.e("Interceptor","La actividad PublicidadActivity está en uso")
                }
                else
                {
                    val publicidadActivityIntent = Intent(this, PublicidadActivity::class.java).apply()
                    {
                    }
                    startActivity(publicidadActivityIntent)
                    Log.e("Interceptor","La actividad PublicidadActivity NO está en uso")
                }
            }
        }
    }
}
