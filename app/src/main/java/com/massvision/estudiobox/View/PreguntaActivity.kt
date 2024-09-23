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
import com.kyanogen.signatureview.SignatureView
import com.massvision.estudiobox.R
import com.massvision.estudiobox.Repository.ApiService
import com.massvision.estudiobox.Repository.RetrofitHelper
import kotlinx.android.synthetic.main.activity_pregunta.*
import android.graphics.Bitmap
import android.util.Base64
import java.io.ByteArrayOutputStream

//private val INACTIVITY_TIMEOUT: Long = 5 * 60 * 1000 // 5 minutos de inactividad
private var INACTIVITY_TIMEOUT: Long = 1 * 60 * 100 // 6 segundos de inactividad
private var inactivityTimer: CountDownTimer? = null
private var publicidadBase64: String = ""
private var idEncuestaGlobal: Int = 0

class PreguntaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pregunta)
        publicidadBase64 = ""
        val bundle= intent.extras
        //setup(email?:"",provider?:"")
        val idEncuesta = bundle?.getInt("idEncuesta")
        val titulo = bundle?.getString("titulo")
        val descripcion = bundle?.getString("descripcion")
        val permiteFirma = bundle?.getString("permiteFirma")
        val permiteDatoAdicional = bundle?.getString("permiteDatoAdicional")
        val tiempoDeEspera = bundle?.getInt("tiempoDeEspera")
        val email = bundle?.getString("email")
        val idEmpresa = bundle?.getInt("idEmpresa")
        //Datos del cliente
        val correoClt = bundle?.getString("correoClt")
        val generoClt = bundle?.getString("generoClt")
        val fechaNacClt = bundle?.getString("fechaNacClt")
        Log.d("Interceptor","----------------------------")
        Log.d("Interceptor","idEncuesta: "+idEncuesta)
        Log.d("Interceptor","titulo: "+titulo)
        Log.d("Interceptor","descripcion: "+descripcion)
        Log.d("Interceptor","permiteFirma: "+permiteFirma)
        Log.d("Interceptor","permiteDatoAdicional: "+permiteDatoAdicional)
        Log.d("Interceptor","tiempoDeEspera: "+tiempoDeEspera)
        Log.d("Interceptor","email: "+email)
        Log.d("Interceptor","idEmpresa: "+idEmpresa)
        Log.d("Interceptor","correoClt: "+correoClt)
        Log.d("Interceptor","generoClt: "+generoClt)
        Log.d("Interceptor","fechaNacClt: "+fechaNacClt)
        Log.d("Interceptor","----------------------------")
        if (idEncuesta != null && titulo!= null && descripcion!= null && permiteFirma!= null
            && permiteDatoAdicional!= null && tiempoDeEspera!= null && email!= null&& idEmpresa!= null
            && correoClt!= null && generoClt!= null && fechaNacClt!= null) {
            idEncuestaGlobal = idEncuesta
            getPublicidad(idEncuestaGlobal)
            setup(idEncuesta,titulo,descripcion,permiteFirma,permiteDatoAdicional,tiempoDeEspera,email,idEmpresa,correoClt,generoClt,fechaNacClt)
        }
        Log.d("Interceptor","idEncuesta: "+idEncuestaGlobal+" email:"+email)

        // Inicializar el temporizador de inactividad
        if (tiempoDeEspera != null) {
            inactivityTimer = object : CountDownTimer(tiempoDeEspera.toLong(), 1000) {

                override fun onTick(millisUntilFinished: Long) {
                    // Nada que hacer en cada tick
                }
                override fun onFinish() {
                    // Acción a ejecutar cuando el temporizador llega a cero
                    Log.d("Interceptor","---Inactividad---"+tiempoDeEspera/1000)
                    getViewPublicidad()
                }
            }.start()
        }
    }
    override fun onUserInteraction() {
        // Reiniciar el temporizador de inactividad cada vez que se detecte una interacción del usuario
        Log.d("Interceptor","+++Actividad+++")
        inactivityTimer?.cancel()
        inactivityTimer?.start()
    }
    private fun setup(idEncuesta:Int,titulo:String,descripcion:String,permiteFirma:String,permiteDatoAdicional:String,tiempoDeEspera: Int,email:String,idEmpresa:Int,correoClt:String,generoClt:String,fechaNacClt:String)
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
        //consultamos preguntas
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
                        //Encabezado de la encuesta-Titulo
                        val textEncuesta = TextView(this@PreguntaActivity)
                        textEncuesta.textSize = 25f
                        textEncuesta.setGravity(Gravity.CENTER_VERTICAL or Gravity.CENTER_HORIZONTAL)
                        textEncuesta.setLayoutParams(generalLayout)
                        textEncuesta.setText(titulo)
                        textEncuesta.setBackgroundColor(Color.TRANSPARENT)
                        textEncuesta.setTextColor(Color.BLACK)
                        textEncuesta.setTypeface(null, Typeface.BOLD)// Establecer estilo negrita
                        cardLinearLayoutPreguntas.addView(textEncuesta)
                        //Establecemos espacio
                        val spaceEncabezado1 = Space(this@PreguntaActivity)
                        spaceEncabezado1.setLayoutParams(spaceLayout)
                        cardLinearLayoutPreguntas.addView(spaceEncabezado1)
                        //Encabezado de la encuesta-Descripcion
                        val textEncuestaDesc = TextView(this@PreguntaActivity)
                        textEncuestaDesc.textSize = 20f
                        textEncuestaDesc.setGravity(Gravity.CENTER_VERTICAL or Gravity.CENTER_HORIZONTAL)
                        textEncuestaDesc.setLayoutParams(generalLayout)
                        textEncuestaDesc.setText(descripcion)
                        textEncuestaDesc.setBackgroundColor(Color.TRANSPARENT)
                        textEncuestaDesc.setTextColor(Color.BLACK)
                        //textEncuestaDesc.setTypeface(null, Typeface.BOLD)// Establecer estilo negrita
                        cardLinearLayoutPreguntas.addView(textEncuestaDesc)
                        //Establecemos espacio
                        val spaceEncabezado2 = Space(this@PreguntaActivity)
                        spaceEncabezado2.setLayoutParams(spaceLayout)
                        cardLinearLayoutPreguntas.addView(spaceEncabezado2)
                        val spaceEncabezado3 = Space(this@PreguntaActivity)
                        spaceEncabezado3.setLayoutParams(spaceLayout)
                        cardLinearLayoutPreguntas.addView(spaceEncabezado3)
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
                                    // Crear un GridLayout para organizar los botones y números en una fila
                                    val gridLayout = GridLayout(this@PreguntaActivity)
                                    gridLayout.layoutParams = generalLayout
                                    gridLayout.columnCount = 11 // Establecer 11 columnas para los 11 números (0 al 10)
                                    gridLayout.orientation = GridLayout.HORIZONTAL // Asegurar que sea horizontal

                                    // Lista para almacenar los RadioButtons y gestionar la selección
                                    val radioButtonList = mutableListOf<RadioButton>()

                                    // Crear RadioButtons del 0 al 10 en el GridLayout
                                    for (i in 0..10) {
                                        // Crear un LinearLayout vertical para organizar botón y número
                                        val verticalLayout = LinearLayout(this@PreguntaActivity)
                                        verticalLayout.orientation = LinearLayout.VERTICAL
                                        verticalLayout.layoutParams = GridLayout.LayoutParams()
                                        verticalLayout.gravity = Gravity.CENTER // Centrar todo dentro de cada celda

                                        // Crear un RadioButton
                                        val radioButton = RadioButton(this@PreguntaActivity)
                                        radioButton.id = View.generateViewId() // Generar un ID único para cada botón
                                        radioButton.layoutParams = LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.WRAP_CONTENT,
                                            LinearLayout.LayoutParams.WRAP_CONTENT
                                        )
                                        radioButton.gravity = Gravity.CENTER // Centrar el botón

                                        // Añadir el RadioButton a la lista
                                        radioButtonList.add(radioButton)

                                        // Añadir el RadioButton al LinearLayout vertical
                                        verticalLayout.addView(radioButton)

                                        // Crear un TextView para mostrar el número debajo del botón
                                        val numberTextView = TextView(this@PreguntaActivity)
                                        numberTextView.text = i.toString()
                                        numberTextView.gravity = Gravity.CENTER // Centrar el número
                                        numberTextView.layoutParams = LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.WRAP_CONTENT,
                                            LinearLayout.LayoutParams.WRAP_CONTENT
                                        )

                                        // Añadir el TextView al LinearLayout vertical
                                        verticalLayout.addView(numberTextView)

                                        // Añadir el LinearLayout vertical al GridLayout
                                        gridLayout.addView(verticalLayout)

                                        // Asignar OnClickListener a cada RadioButton
                                        radioButton.setOnClickListener {
                                            // Desmarcar todos los RadioButtons al seleccionar uno
                                            radioButtonList.forEach { it.isChecked = false }
                                            // Marcar el botón actual
                                            radioButton.isChecked = true
                                            // Actualizar el JSON con la respuesta seleccionada
                                            jsonPregunta.addProperty(arrayItem.intIdPregunta.toString(), numberTextView.text.toString())
                                        }
                                    }
                                    // Añadir el GridLayout al layout principal
                                    cardLinearLayoutPreguntas.addView(gridLayout)
                                    //jsonPregunta.addProperty(arrayItem.intIdPregunta.toString(), selectedValue.toString())

                                    /*val respIpnButton = Button(this@PreguntaActivity)
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
                                    cardLinearLayoutPreguntas.addView(ipnSeekBar)*/
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
                        cardViewPreguntas.addView(cardLinearLayoutPreguntas)
                        preguntaLayoutSecundario.addView(cardViewPreguntas)
                        //Datos Adicionales
                        if(permiteDatoAdicional=="Si") {
                            //Creamos el cardView de Datos Adicionales
                            val cardViewDA = CardView(this@PreguntaActivity)
                            cardViewDA.radius = 15f
                            cardViewDA.radius = 25f
                            val color =
                                ContextCompat.getColor(this@PreguntaActivity, android.R.color.white)
                            cardViewDA.setCardBackgroundColor(color)
                            cardViewDA.setContentPadding(36, 36, 36, 36)
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
                            textDatosAdicionales.setTypeface(
                                null,
                                Typeface.BOLD
                            )// Establecer estilo negrita
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
                            spinnerGenero.onItemSelectedListener =
                                object : AdapterView.OnItemSelectedListener {
                                    //((TextView) adapterView.getChildAt(0)).setGravity(Gravity.CENTER)
                                    override fun onNothingSelected(parent: AdapterView<*>?) {
                                        Toast.makeText(
                                            this@PreguntaActivity,
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
                                "strGenero", ""
                            )
                            // Obtener el valor del CheckBox seleccionado Masculino
                            checkBoxGeneroMasc.setOnCheckedChangeListener { buttonView, isChecked ->
                                if (isChecked) {
                                    // Checkbox Masculino seleccionado
                                    checkBoxGeneroFem.isChecked =
                                        false // Deseleccionar Checkbox Femenino
                                    jsonDataRespuesta.addProperty(
                                        "strGenero", "Masculino"
                                    )
                                } else {
                                    // Checkbox Masculino no seleccionado
                                    jsonDataRespuesta.addProperty(
                                        "strGenero", ""
                                    )
                                }
                            }
                            // Obtener el valor del CheckBox seleccionado Femenino
                            checkBoxGeneroFem.setOnCheckedChangeListener { buttonView, isChecked ->
                                if (isChecked) {
                                    // Checkbox Femenino seleccionado
                                    checkBoxGeneroMasc.isChecked =
                                        false // Deseleccionar Checkbox Femenino
                                    jsonDataRespuesta.addProperty(
                                        "strGenero", "Femenino"
                                    )
                                } else {
                                    // Checkbox Femenino no seleccionado
                                    jsonDataRespuesta.addProperty(
                                        "strGenero", ""
                                    )
                                }
                            }
                            cardLinearLayoutDA.addView(checkBoxGeneroMasc)
                            cardLinearLayoutDA.addView(checkBoxGeneroFem)
                            //cardLinearLayoutDA.addView(spinnerGenero)
                            cardViewDA.addView(cardLinearLayoutDA)
                            preguntaLayoutSecundario.addView(cardViewDA)
                        }
                        else if(correoClt!="" && generoClt!="" && fechaNacClt!="")
                        {
                            jsonDataRespuesta.addProperty("strCorreo",correoClt)
                            jsonDataRespuesta.addProperty("strGenero", generoClt)
                            jsonDataRespuesta.addProperty("strEdad",fechaNacClt)
                        }
                        jsonDataRespuesta.addProperty("strUsrSesion", email)
                        //Validamos si debemos presentar la firma
                        //if(titulo == "Satisfacción paciente por MSP")
                        if(permiteFirma == "Si")
                        {
                            //Configuramos para que aparezca la firma
                            val firmaCardView = findViewById<CardView>(R.id.firmaCardView)
                            firmaCardView.visibility = View.VISIBLE
                            //Establecemos un texto de ayuda
                            val textFirma = TextView(this@PreguntaActivity)
                            textFirma.textSize = 17f
                            textFirma.setGravity(Gravity.LEFT or Gravity.LEFT)
                            textFirma.setLayoutParams(generalLayout)
                            textFirma.setText("Ingrese su Firma:")
                            textFirma.setBackgroundColor(Color.TRANSPARENT)
                            textFirma.setTextColor(Color.BLACK)
                            textFirma.setTypeface(null, Typeface.BOLD)// Establecer estilo negrita
                            firmaCardView.addView(textFirma)
                            Log.d("Interceptor", "No Ocultar Firma")
                            //Instanciamos el objeto de la firma para que aparezca
                            val firmaSignatureView = findViewById<SignatureView>(R.id.firmaSignatureView)
                            firmaSignatureView.visibility = View.VISIBLE
                        }
                        else
                        {
                            Log.d("Interceptor", "Ocultar Firma")
                            //Configuramos para que no aparezca la firma
                            val firmaCardView = findViewById<CardView>(R.id.firmaCardView)
                            firmaCardView.visibility = View.GONE
                            //Instanciamos el objeto de la firma para que no aparezca
                            val firmaSignatureView = findViewById<SignatureView>(R.id.firmaSignatureView)
                            firmaSignatureView.visibility = View.GONE
                        }
                        //Guardar Encuesta
                        buttonGuardarEncuesta.setOnClickListener {
                            if(permiteFirma == "Si") {
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
                                    jsonDataRespuesta.addProperty("strFirma", "")
                                } else {
                                    Log.d("Interceptor", "Firma llena")
                                    jsonDataRespuesta.addProperty("strFirma", base64String)
                                }
                            }
                            val jsonObjectRespuesta = JsonObject()
                            jsonObjectRespuesta.add("data",jsonDataRespuesta)
                            createRespuesta(jsonObjectRespuesta, idEncuestaGlobal,titulo,descripcion,permiteFirma,permiteDatoAdicional,tiempoDeEspera,email,idEmpresa)
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
                        .setContentText("Ha ocurrido un error, al tratar de obtener las preguntas de la encuesta")
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
    private fun createRespuesta(jsonObject:JsonObject,idEncuesta: Int,titulo:String,descripcion:String,strPermiteFirma:String,strPermiteDatoAdicional:String,intTiempo: Int,email:String,idEmpresa:Int)
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
                                    if(idEmpresa==100)
                                    {
                                        getViewDatosPersonales(idEncuesta,titulo,descripcion,strPermiteFirma,strPermiteDatoAdicional,intTiempo,email,idEmpresa)
                                    }
                                    else
                                    {
                                        startActivity(getIntent());
                                    }
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
                                .setContentText("Ha ocurrido un error, al tratar de ingresar las respuestas")
                                .show()
                        }
                    } else {
                        pDialog.hide()
                        SweetAlertDialog(this@PreguntaActivity, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error")
                            .setContentText("Ha ocurrido un error, al tratar de realizar conexión con el servidor")
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
    private fun getPublicidad(idEncuesta:Int)
    {
        Log.d("Interceptor","-------getPublicidad-------")
        publicidadBase64 = ""
        /*val pDialog = SweetAlertDialog(this@PreguntaActivity, SweetAlertDialog.PROGRESS_TYPE)
        pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
        pDialog.titleText = "Cargando ..."
        pDialog.setCancelable(true)
        pDialog.show()*/
        //consumo ApiRest
        Log.d("Interceptor","consumo ApiRest")
        val apiService = RetrofitHelper.getInstance().create(ApiService::class.java)
        val jsonDataPublicidad = JsonObject()
        jsonDataPublicidad.addProperty("intIdEncuesta",idEncuesta)
        jsonDataPublicidad.addProperty("estado","Activo")
        val jsonObjectPublicidad = JsonObject()
        jsonObjectPublicidad.add("data",jsonDataPublicidad)
        lifecycleScope.launchWhenCreated {
            try {
                val responsePublicidad = apiService.getPublicidad(jsonObjectPublicidad)
                if (responsePublicidad.isSuccessful()) {
                    //pDialog.hide()
                    Log.d("Interceptor", "resultado del getPublicidad isSuccessful")
                    Log.d("Interceptor", "Response: " + responsePublicidad.body().toString())
                    if (responsePublicidad.body()?.intStatus == 200 && responsePublicidad.body()?.arrayPublicidad?.arrayArchivos?.isNotEmpty() == true)
                    {
                        for (arrayItemPublicidad in responsePublicidad.body()?.arrayPublicidad?.arrayArchivos!!) {
                            Log.d("Interceptor", "NombreArchivo: " + arrayItemPublicidad.strNombreArc)
                            publicidadBase64 = arrayItemPublicidad.strBase64Image
                        }
                    }
                    else
                    {
                        publicidadBase64 = ""
                    }
                } else {
                    publicidadBase64 = ""
                    //pDialog.hide()
                    SweetAlertDialog(this@PreguntaActivity, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Error")
                        .setContentText("Ha ocurrido un error, al tratar de realizar conexión con el servidor")
                        .show()
                }
            } catch (Ex: Exception) {
                //pDialog.hide()
                SweetAlertDialog(this@PreguntaActivity, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error")
                    .setContentText(Ex.localizedMessage)
                    .show()
                Log.e("Interceptor", Ex.localizedMessage)

            }
        }
    }
    private fun getViewPublicidad()
    {
        Log.d("Interceptor","getViewPublicidad")
        inactivityTimer?.cancel()
        val am = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val tasks = am.getRunningTasks(Int.MAX_VALUE)
        if (tasks.isNotEmpty()) {
            val topActivity = tasks[0].topActivity
            if (topActivity != null) {
                if (topActivity.packageName == packageName && topActivity.className == PublicidadActivity::class.java.name) {
                    Log.d("Interceptor","La actividad PublicidadActivity está en uso")
                }
                else
                {
                    Log.d("Interceptor","publicidadBase64: "+publicidadBase64)
                    if(publicidadBase64!="")
                    {
                        Log.d("Interceptor","Inicializo la Actividad Publicidad")
                        val publicidadActivityIntent = Intent(this, PublicidadActivity::class.java).apply()
                        {
                            putExtra("publicidadBase64",publicidadBase64)
                        }
                        startActivity(publicidadActivityIntent)
                    }
                    Log.d("Interceptor","La actividad PublicidadActivity NO está en uso")
                    Log.d("Interceptor","idEncuestaGlobal: "+idEncuestaGlobal)
                    getPublicidad(idEncuestaGlobal)
                }
            }
        }

    }
    private fun getViewDatosPersonales(idEncuesta: Int,titulo:String,descripcion:String,strPermiteFirma:String,strPermiteDatoAdicional:String,intTiempo: Int,email:String,idEmpresa: Int)
    {
        Log.d("Interceptor","getViewDatosPersonales")
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
}
