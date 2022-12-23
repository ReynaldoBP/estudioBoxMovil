package com.massvision.estudiobox.View

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_inicio.*
import com.massvision.estudiobox.R

enum class ProviderType
{
    BASIC,
    GOOGLE
}

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio)
        //Logica para ingresar por correo-firebase
        val bundle= intent.extras
        val email = bundle?.getString("email")
        val provider = bundle?.getString("provider")
        setup(email?:"",provider?:"")
        //Guardar la sesión del usuario autenticado
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email",email)
        prefs.putString("provider",provider)
        prefs.apply()
    }
    private fun setup(email:String,provider:String)
    {
        title = "Menú"
        emailtextView.text = "Bienvenido, "+email
        //providertextView.text = provider
        //Botón de Cerrar Sesión
        logOutButton.setOnClickListener {
            //Borrado de datos
            val prefs = getSharedPreferences(getString(R.string.prefs_file),Context.MODE_PRIVATE).edit()
            prefs.clear()
            prefs.apply()
            //Cerrar session
            FirebaseAuth.getInstance().signOut()
            onBackPressed()//volver a la pantalla anterior
        }
        //Botón de Empresas
        empresaButton.setOnClickListener {
            val companyListIntent = Intent(this, EmpresaActivity::class.java).apply()
            {
            }
            startActivity(companyListIntent)
        }
    }
}