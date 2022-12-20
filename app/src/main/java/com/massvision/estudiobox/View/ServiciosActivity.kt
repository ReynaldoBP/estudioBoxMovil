package com.massvision.estudiobox.View

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.massvision.estudiobox.R
import kotlinx.android.synthetic.main.activity_servicios.*

class ServiciosActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_servicios)
        setup()
    }
    private fun setup()
    {
        title = "Listado de Servicios"
        servicioAtencionClienteButton.setOnClickListener {
            val surveyActivityListIntent = Intent(this, EncuestaActivity::class.java).apply()
            {
            }
            startActivity(surveyActivityListIntent)
        }
        cobranzaButton.setOnClickListener {
            val surveyActivityListIntent = Intent(this, EncuestaActivity::class.java).apply()
            {
            }
            startActivity(surveyActivityListIntent)
        }
    }
}