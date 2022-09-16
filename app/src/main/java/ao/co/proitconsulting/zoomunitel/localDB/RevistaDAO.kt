package ao.co.proitconsulting.zoomunitel.localDB

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ao.co.proitconsulting.zoomunitel.models.RevistaModel
import kotlinx.coroutines.flow.Flow

@Dao
interface RevistaDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(revistas: List<RevistaModel>)

    @Query("SELECT * FROM revistas ORDER BY uid DESC")
    fun getAllRevistas(): Flow<List<RevistaModel>>

    @Query("DELETE FROM revistas")
    suspend fun deleteRevistas()


}