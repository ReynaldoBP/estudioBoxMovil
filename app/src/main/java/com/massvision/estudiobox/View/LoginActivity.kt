package com.massvision.estudiobox.View

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.gson.JsonObject
import com.massvision.estudiobox.R
import com.massvision.estudiobox.Repository.ApiService
import com.massvision.estudiobox.Repository.RetrofitHelper
import kotlinx.android.synthetic.main.activity_crear_cuenta.*
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    private val GOOGLE_SIGN_IN = 100
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        //Analytics Event
        val analytics:FirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("Mensaje","Integracion de Firebase completa")
        analytics.logEvent("InitScreen",bundle)
        setup()
        session()
    }

    override fun onStart() {
        super.onStart()
        authLayout.visibility = View.VISIBLE
    }
    private fun session()
    {
        Log.d("Interceptor","Sesion")
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email = prefs.getString("email", null)
        val idCliente = prefs.getInt("idCliente", 0)
        Log.d("Interceptor","email: "+email+" idCliente: "+idCliente)
        if(email!=null && idCliente!=null)
        {
            authLayout.visibility = View.INVISIBLE
            showHome(email,idCliente)
        }
    }
    private fun setup()
    {
        title=""
        bienvenidoTextView.textSize = 20f
        bienvenidoTextView.setTextColor(Color.BLACK)
        bienvenidoTextView.setTypeface(Typeface.SANS_SERIF, Typeface.NORMAL)
        signUpButton.setOnClickListener {
            val crearCuentaActivityListIntent = Intent(this, CrearCuentaActivity::class.java).apply()
            {
            }
            startActivity(crearCuentaActivityListIntent)

            /*if(emailEditText.text.isNotEmpty() && passwordEditText.text.isNotEmpty())
            {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(emailEditText.text.toString(),
                    passwordEditText.text.toString()).addOnCompleteListener()
                {
                    if(it.isSuccessful)
                    {
                        showHome(it.result?.user?.email?:"", ProviderType.BASIC)
                    }
                    else
                    {
                        showAlert()
                    }
                }
            }*/
        }
        loginButton.setOnClickListener {
            val pDialog = SweetAlertDialog(this@LoginActivity, SweetAlertDialog.PROGRESS_TYPE)
            pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
            pDialog.titleText = "Cargando ..."
            pDialog.setCancelable(true)
            pDialog.show()
            if(emailEditText.text.isNotEmpty() && passwordEditText.text.isNotEmpty())
            {
                val apiService = RetrofitHelper.getInstance().create(ApiService::class.java)
                val jsonData = JsonObject()
                jsonData.addProperty("strCorreo",emailEditText.text.toString())
                jsonData.addProperty("strContrasenia",passwordEditText.text.toString())
                val jsonObject = JsonObject()
                jsonObject.add("data",jsonData)
                Log.d("Interceptor","Request: "+jsonObject)
                lifecycleScope.launchWhenCreated {
                    try {
                        val response = apiService.getLogin(jsonObject)
                        Log.d("Interceptor","resultado del getLogin isSuccessful")
                        Log.d("Interceptor","ResponseLogin: "+response.body().toString())
                        if (response.isSuccessful()) {
                            pDialog.hide()
                            if(response.body()?.intStatus==200)
                            {
                                response.body()?.arrayCliente?.intIdCliente?.let { it1 ->
                                    showHome(emailEditText.text.toString(),
                                        it1.toInt())
                                }
                                emailEditText.setText("")
                                passwordEditText.setText("")
                            }
                            else if(response.body()?.intStatus==204)
                            {
                                SweetAlertDialog(this@LoginActivity, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Error")
                                    .setContentText(response.body()?.strMensaje)
                                    .show()
                            }
                            else{
                                SweetAlertDialog(this@LoginActivity, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Error")
                                    .setContentText("Ha ocurrido un error, por favor inténtalo de nuevo más tarde")
                                    .show()
                            }
                        } else {
                            pDialog.hide()
                            SweetAlertDialog(this@LoginActivity, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Error")
                                .setContentText("Ha ocurrido un error, por favor inténtalo de nuevo más tarde")
                                .show()
                        }
                    }catch (Ex:Exception){
                        pDialog.hide()
                        SweetAlertDialog(this@LoginActivity, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error")
                            .setContentText("Ha ocurrido un error, por favor inténtalo de nuevo más tarde")
                            .show()
                        Log.e("Interceptor",Ex.localizedMessage)
                    }
                }

                /*FirebaseAuth.getInstance().signInWithEmailAndPassword(emailEditText.text.toString(),
                    passwordEditText.text.toString()).addOnCompleteListener()
                {
                    if(it.isSuccessful)
                    {
                        showHome(it.result?.user?.email?:"", ProviderType.BASIC)
                    }
                    else
                    {
                        showAlert()
                    }
                }*/
            }
            else
            {
                pDialog.hide()
                SweetAlertDialog(this@LoginActivity, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error")
                    .setContentText("Por favor ingresa tu correo electrónico y contraseña")
                    .show()
            }
        }
        googleButton.setOnClickListener {
            //Configuracion para entrar en sesión con correo electronico
            val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                             .requestIdToken(getString(R.string.default_web_client_id))
                             .requestEmail().build()
            val googleClient = GoogleSignIn.getClient(this,googleConf)
            googleClient.signOut()
            startActivityForResult(googleClient.signInIntent,GOOGLE_SIGN_IN)
        }
    }
    private fun showAlert()
    {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error autenticando al usuario!")
        builder.setPositiveButton("Aceptar",null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
    private fun showHome(email:String,idCliente:Int)
    {
        val empresaActivityIntent = Intent(this, EmpresaActivity::class.java).apply()
        {
            putExtra("email",email)
            putExtra("idCliente",idCliente)
        }
        startActivity(empresaActivityIntent)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                    FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                showHome(account.email ?: "")
                            } else {
                                showAlert()
                            }
                        }
                }
            }
            catch (e:ApiException)
            {
                showAlert()
            }
        }
    }
}