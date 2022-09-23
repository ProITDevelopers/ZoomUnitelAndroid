package ao.co.proitconsulting.zoomunitel.ui.repository

import androidx.room.withTransaction
import ao.co.proitconsulting.zoomunitel.api.RetrofitInstance
import ao.co.proitconsulting.zoomunitel.helpers.networkBoundResource
import ao.co.proitconsulting.zoomunitel.localDB.RevistaDatabase

class RevistaRepository(private val db:RevistaDatabase) {

    private val revistaDAO = db.getRevistaDAO()

    fun getRevistasHome() = networkBoundResource(
        query = {
            revistaDAO.getAllRevistas()
        },
        fetch = {
            RetrofitInstance.api.getTodasRevistas()
        },
        saveFetchResult = { revistaList->

            db.withTransaction {
                revistaDAO.deleteRevistas()
                revistaDAO.insertOrUpdate(revistaList)
            }

        }
    )

}