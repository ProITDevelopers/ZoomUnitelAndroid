package ao.co.proitconsulting.zoomunitel.localDB

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ao.co.proitconsulting.zoomunitel.models.RevistaModel

@Dao
interface RevistaDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(revista: RevistaModel)

    @Query("SELECT * FROM revistas")
    fun getAllRevistas(): LiveData<List<RevistaModel>>

    @Query("DELETE FROM revistas")
    suspend fun deleteRevistas()
}