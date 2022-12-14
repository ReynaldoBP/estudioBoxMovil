package com.massvision.estudiobox

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_company_list.*

class CompanyListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_company_list)
        setup()
    }
    private fun setup()
    {
        title = "Listado de Empresas"
        artefactaTestButton.setOnClickListener {
            val servicesListIntent = Intent(this,ServicesListActivity::class.java).apply()
            {
            }
            startActivity(servicesListIntent)
        }
    }
}