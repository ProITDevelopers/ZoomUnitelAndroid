package ao.co.proitconsulting.zoomunitel.ui.repository

import ao.co.proitconsulting.zoomunitel.api.RetrofitInstance
import ao.co.proitconsulting.zoomunitel.localDB.RevistaDatabase

class RevistaRepository(val db:RevistaDatabase) {

    suspend fun getRevistasHome() =
        RetrofitInstance.api.getTodasRevistas()
}