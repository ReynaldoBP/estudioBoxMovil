package com.massvision.estudiobox.View

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.massvision.estudiobox.R
import com.massvision.estudiobox.Repository.ApiService
import com.massvision.estudiobox.Repository.RetrofitHelper
import kotlinx.android.synthetic.main.activity_empresa.*
import org.json.JSONObject

class EmpresaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_empresa)
        setup()
    }
    private fun setup()
    {
        title = "Listado de Empresas"
        artefactaTestButton.setOnClickListener {



            val encuestaActivityIntent = Intent(this, EncuestaActivity::class.java).apply()
            {
            }
            startActivity(encuestaActivityIntent)


            /*val servicesListIntent = Intent(this, ServiciosActivity::class.java).apply()
            {
            }
            startActivity(servicesListIntent)*/
        }
    }
}