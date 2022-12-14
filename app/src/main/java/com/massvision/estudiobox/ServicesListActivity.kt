package com.massvision.estudiobox

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_company_list.*
import kotlinx.android.synthetic.main.activity_services_list.*

class ServicesListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_services_list)
        setup()
    }
    private fun setup()
    {
        title = "Listado de Servicios"
        servicioAtencionClienteButton.setOnClickListener {
            val surveyActivityListIntent = Intent(this,SurveyActivity::class.java).apply()
            {
            }
            startActivity(surveyActivityListIntent)
        }
        cobranzaButton.setOnClickListener {
            val surveyActivityListIntent = Intent(this,SurveyActivity::class.java).apply()
            {
            }
            startActivity(surveyActivityListIntent)
        }
    }
}