package com.jorgemartinez.tictactoe.model

import com.google.type.DateTime
import java.util.*
import kotlin.collections.ArrayList

class Jugada  {

   lateinit var jugadorUnoId:String

     lateinit var jugadorDosId:String
     lateinit var celdasSeleccionadas: ArrayList<Int>
     var turnoJugadorUno:Boolean = true
     lateinit var ganadorId:String
     lateinit var created: Date
     lateinit  var abandonoId: String



        constructor():this("", "", ArrayList<Int>() ,true,"", Date(),""){

        }

    constructor(jugadorUnoId: String) {
        this.jugadorUnoId = jugadorUnoId
        this.jugadorDosId = ""
        celdasSeleccionadas = ArrayList()
        for (i  in 0 .. 9){
            celdasSeleccionadas.add(i)
        }
        this.turnoJugadorUno = true
        this.created = Date()
        this.ganadorId = ""
        this.abandonoId = ""
    }
    //En constructor secundario no está permitido el val ni var
    constructor(jugadorUnoId:String, jugadorDosId:String,celdasSeleccionadas: ArrayList<Int>,
                turnoJugadorUno:Boolean, ganadorId:String,created: Date,abandonoId: String
    )


}