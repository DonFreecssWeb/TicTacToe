package com.jorgemartinez.tictactoe.ui

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.ktx.Firebase

import com.jorgemartinez.tictactoe.R
import com.jorgemartinez.tictactoe.app.Constantes
import com.jorgemartinez.tictactoe.model.Jugada
import kotlinx.coroutines.Runnable

private lateinit var tvLoadingMessage: TextView
private lateinit var progressBar: ProgressBar
private  lateinit var layoutProgressBar: ScrollView
private  lateinit var layoutMenuJuego: ScrollView
private  lateinit  var btnJugar:Button
private  lateinit  var btnRanking:Button

private  lateinit var firebaseAuth: FirebaseAuth
private  lateinit var db: FirebaseFirestore
private  lateinit var firebaseUser: FirebaseUser
private lateinit var uid: String
private  lateinit var jugadaId:String
//nulo porque haya encontrado partida y entre como jugador2
private   var listenerRegistration: ListenerRegistration? = null
var contador = 0


class FindGameActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_game)

        tvLoadingMessage  = findViewById(R.id.textViewLoading)
        progressBar = findViewById(R.id.progressBarJugadas)
        btnJugar = findViewById(R.id.buttonJugar)
        btnRanking = findViewById(R.id.buttonRanking)

        initProgressBar()
        initFirebase()
        eventos()



    }

    private fun initFirebase() {
        //inicializar FireBase ?

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = firebaseAuth.currentUser!!
        uid = firebaseUser.uid
        //uid = firebaseAuth.currentUser!!.uid
        db = FirebaseFirestore.getInstance()

    }


    private fun eventos() {
        btnJugar.setOnClickListener {
            changeMenuVisibility(false)
            buscarJugadaLibre()
        }

        btnRanking.setOnClickListener {

        }
    }


    private fun buscarJugadaLibre() {
        tvLoadingMessage.text = "Buscando una jugada libre ..."
        //Buscamos un campo donde jugadorDosId esté vacío, es decir que no haya contrincante
        db.collection("jugadas").whereEqualTo("jugadorDosId","")
            .get().addOnCompleteListener {
                if (it.result.size() == 0){
                    //si no encuentra query
                    //creamos una nueva jugada
                    //para que el usuario se convierta en jugador1 y ya no en jugador2

                    crearNuevaJugada()
                }else{
                    //solo cogemos el primero, si nos devuelve una lista de jugadas donde haya jugadores libres,
                        // solo escogemos el primero
                    val docJugada:DocumentSnapshot = it.result.documents.get(0)
                    //obtenemos el identificador único del documento de jugadas donde no hay jugador2
                    jugadaId = docJugada.id  // id = docmanual

                    //pasamos la lista al objeto jugada
                    //Los campos que recibimos en el DocumentSnapshot son los mismo que la clase
                    //no va haber problema del PARSEO
                    val jugada = docJugada.toObject(Jugada::class.java)
                    //agregamos nuestro identificador de usuario o de jugador al jugador2
                    jugada?.jugadorDosId = uid

                    //actualizar la jugada para que se almacene en la base de datos el nuevo jugador 2 y no le aparezca
                    //a otros jugadores como una jugada libre
                    //nosotros estamos completando la jugada
                    if (jugada != null) {
                        db.collection("jugadas")
                            .document(jugadaId) //docmanual
                            .set(jugada)
                            .addOnSuccessListener {
                                startGame()
                            }.addOnFailureListener{

                                changeMenuVisibility(true)
                                Toast.makeText(applicationContext,"Hubo algún error al entrar a la jugada",Toast.LENGTH_LONG).show()
                            }
                    }


                }
            }
    }


    private fun crearNuevaJugada() {
        tvLoadingMessage.text ="Creando una jugada nueva"
        val nuevaJugada = Jugada(uid)

        //con la base de datos vacía, se crea la colección jugadas con un identificador único aleatorio
        //y se establecen los campos por defecto de clase con la diferencia que tenemos
        //como jugadorUnoId se establece el uid que es el identificador del usuario (email y contraseña)
        db.collection("jugadas").add(nuevaJugada)
            .addOnSuccessListener {
                jugadaId = it.id  //el id del documento

                //tenemos creada la jugada, debemos esperar al jugador2
                esperarJugador()
            }.addOnFailureListener{
                changeMenuVisibility(true)
                Toast.makeText(applicationContext,"Error al crear",Toast.LENGTH_LONG).show()
            }
    }


    private fun esperarJugador() {
        tvLoadingMessage.text = "Esperando a otro jugador"

       //addSnapshotListener se entera de cuando entra un nuevo jugador (jugador2)
        //es decir cuando hay un cambio
        //TODO por algún motivo con 1 jugador entra adentro del snapshotListener
        listenerRegistration = db.collection("jugadas")
           .document(jugadaId)
            .addSnapshotListener { snapshot, error ->
                contador++
                if (snapshot != null && snapshot.exists()) {
                    //SI NO ES VACIO, quiere decir que ha entrado el segundo jugador
                    if (!snapshot.get("jugadorDosId")!!.equals("")){
                        tvLoadingMessage.text = "!Ya ha llegado un jugador! Comienza la partida"
                        //damos un delay para que el usuario lea el mensaje

                            Handler(mainLooper).postDelayed({
                                startGame()
                            },1500)}


                } else {
                    Log.d("TAG", "Current data: null")
                }
            }
    }

    private fun startGame() {
        if (listenerRegistration != null){

            Log.d("TAG", "Contador: $contador")

            //para que no esté escuchando una vez comenzado el juego y ya con ambos jugadores
            listenerRegistration!!.remove()
        }
        val intent = Intent(applicationContext,GameActivity::class.java).apply {
            putExtra(Constantes.EXTRA_JUGADA_ID, jugadaId)
        }
        startActivity(intent)

    }

    private fun initProgressBar() {
        layoutProgressBar = findViewById(R.id.layoutprogressBar)
        layoutMenuJuego = findViewById(R.id.menuJuego)
        progressBar.isIndeterminate = true
        tvLoadingMessage.text = "Cargando..."

        changeMenuVisibility(true)
    }

    private fun changeMenuVisibility(showMenu: Boolean) {
        //que aparezca el menu del juego por defecto
        layoutProgressBar.visibility = if (showMenu) View.GONE else View.VISIBLE
        layoutMenuJuego.visibility = if (showMenu) View.VISIBLE else View.GONE
    }

    override fun onResume() {
        super.onResume()
        //que aparezca el menu del juego por defecto
        changeMenuVisibility(true)

    }
}