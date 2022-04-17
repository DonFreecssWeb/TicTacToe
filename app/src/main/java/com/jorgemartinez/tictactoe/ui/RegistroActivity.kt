package com.jorgemartinez.tictactoe.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

import com.jorgemartinez.tictactoe.R
import com.jorgemartinez.tictactoe.model.User

class RegistroActivity : AppCompatActivity() {
    lateinit var etName:EditText
    lateinit var etEmail:EditText
    lateinit var etPass:EditText
    lateinit var btnRegistro:Button
    lateinit var firebaseAuth:FirebaseAuth
    lateinit var db:FirebaseFirestore
    lateinit var name:String
    lateinit var email:String
    lateinit var password:String
    lateinit var pbRegistro:ProgressBar
    lateinit var formRegistro:ScrollView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        etEmail  = findViewById(R.id.editTextTextEmail)
        etPass  = findViewById(R.id.editTextTextPassword)
        etName  = findViewById(R.id.editTextTextName)
        btnRegistro = findViewById(R.id.buttonRegistro)
        formRegistro = findViewById(R.id.formRegistro)
        pbRegistro = findViewById(R.id.progressBarRegistro)

        firebaseAuth = FirebaseAuth.getInstance()
         db = Firebase.firestore

       eventos()

        changeLoginFormVisibility(true)
    }

    private fun eventos() {
        btnRegistro.setOnClickListener {
             name = etName.text.toString()
             email = etEmail.text.toString()
             password = etPass.text.toString()

            if (name.isEmpty()){
                etName.error = "El nombre es obligatorio"
            } else if (email.isEmpty()){
                etEmail.error = "El email es obligatorio"
            } else if (password.isEmpty()){
                etPass.error = "La contraseña es obligatorio"
            } else{
                //do
                createUser()
            }
        }

    }

    private fun createUser() {
        changeLoginFormVisibility(false)

        firebaseAuth.createUserWithEmailAndPassword(
            email,password
        ).addOnCompleteListener { respuesta ->
            if (respuesta.isComplete){
                val user = firebaseAuth.currentUser
                updateUI(user)
            }else{
                Toast.makeText(this,"Error en el registro",Toast.LENGTH_LONG).show()
                updateUI(null)
            }
        }

    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null){
            //Almacenar la información del usuario en Firestore
                val nuevoUsuario = User(name,30,40)
            //creamos un documento que justo tiene el identificador del usuario por el email y password
            db.collection("users")
                .document(user.uid)
                .set(nuevoUsuario)
                .addOnSuccessListener {

                    //destruimos pantalla actual de registro
                    finish()
                    //como se destruye el activity, no es necesario para el progress bar

                    //una vez que registramos al usuario podemos navegar a la siguiente pantalla
                    val intent = Intent(this, FindGameActivity::class.java)
                    startActivity(intent)
                }



        }else{
            changeLoginFormVisibility(true)
            etPass.error = "Nombre, Email y/o contraseña incorrectos"
            //foco
            etPass.requestFocus()
        }
    }
    private fun changeLoginFormVisibility(showForm:Boolean) {
        //True  Formulario prendido, progressBar Apagado
        formRegistro.visibility = if (showForm) View.VISIBLE else View.GONE
        pbRegistro.visibility =  if (showForm) View.GONE else View.VISIBLE
    }

    }//class