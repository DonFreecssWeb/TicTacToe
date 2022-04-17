package com.jorgemartinez.tictactoe.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.ScrollView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.jorgemartinez.tictactoe.R
/*
* git remote add origin https://github.com/DonFreecssWeb/TicTacToe.git
git branch -M main
git push -u origin main
* */


class LoginActivity : AppCompatActivity() {
    lateinit var etEmail: EditText
    lateinit var etPassword: EditText
    lateinit var btnLogin:Button
    lateinit var  formLogin:ScrollView
    lateinit var pbLogin:ProgressBar
    lateinit var btnRegistro:Button
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var email:String
    lateinit var password:String
    var tryLogin = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etEmail = findViewById(R.id.editTextTextEmail)
        etPassword = findViewById(R.id.editTextTextPassword)
        btnLogin = findViewById(R.id.buttonLogin)
        formLogin = findViewById(R.id.formLogin)
        pbLogin = findViewById(R.id.progressBar)
        btnRegistro = findViewById(R.id.buttonRegistro)

        firebaseAuth = FirebaseAuth.getInstance()

        eventos()
        changeLoginFormVisibility(true)
    }

    private fun eventos() {
        btnLogin.setOnClickListener {

          email = etEmail.text.toString()
          password = etPassword.text.toString()

            if (email.isEmpty()){
                etEmail.error = "El email es obligatorio"
            } else if (password.isEmpty()){
                etPassword.error = "La contraseña es obligatoria"
            } else{

                //Ocultar formulario
                // se oculta el formulario y aparece el progressBar infinito
                //hasta que la autentificación haya realizado
                changeLoginFormVisibility(false)
                loginUser()

            }
        }

        //registro
        btnRegistro.setOnClickListener {
            val intent = Intent(this, RegistroActivity::class.java)
            startActivity(intent)

            //finihsh()
        }
    }

    private fun loginUser() {

        firebaseAuth.signInWithEmailAndPassword(
            email,password
        ).addOnCompleteListener { respuesta ->
            tryLogin = true
                //si respuesta es correcta
            if (respuesta.isSuccessful){

                //obtenemos el identificador único del usuario
                val user = firebaseAuth.currentUser
                updateUI(user)

            }else{
                Log.w("TAG","singInError: ${respuesta.exception}")
                //lo lanzamos al updateUI pasandole un nulo para que entre al else
                //para volver a mostrar el formulario y pasandole el error al campo
                updateUI(null)
            }
        }

    }

    private fun changeLoginFormVisibility(showForm:Boolean) {
        //True  Formulario prendido, progressBar Apagado
        formLogin.visibility = if (showForm) View.VISIBLE else View.GONE
        pbLogin.visibility =  if (showForm) View.GONE else View.VISIBLE
    }

    private fun updateUI(user: FirebaseUser?) {
        //user es el identificador único del usuario
        if (user != null){
            //Almacenar la información del usuario en Firestore
            //TODO
            val intent = Intent(this, FindGameActivity::class.java)
            Log.w("TAG",firebaseAuth.currentUser!!.uid)
            startActivity(intent)
        }else{
            changeLoginFormVisibility(true)
            //Como es la primera vez, debemos hacer que no se ejecute la primera vez
            //ya que siempre seria null
            if (tryLogin){
                etPassword.error = "Email y/o contraseña incorrectos"
                //foco
                etPassword.requestFocus()
            }

        }
    }

    override fun onStart() {
        //Como es la primera vez, debemos hacer que no se ejecute la primera vez
        //ya que siempre seria null
        super.onStart()
            val currentUser = firebaseAuth.currentUser
            updateUI(currentUser)
    }

}//class