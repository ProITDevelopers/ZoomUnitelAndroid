package ao.co.proitconsulting.zoomunitel.localDB

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ao.co.proitconsulting.zoomunitel.models.RevistaModel

@Database(
    entities = [RevistaModel::class],
    version = 1,
    exportSchema = false
)
abstract class RevistaDatabase : RoomDatabase() {

    abstract fun getRevistaDAO() : RevistaDAO


    companion object{
        private var instance: RevistaDatabase?=null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: createDatabase(context).also {
                instance = it
            }
        }

        private fun createDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                RevistaDatabase::class.java,
                "revista_db.db"
            ).build()
    }
}