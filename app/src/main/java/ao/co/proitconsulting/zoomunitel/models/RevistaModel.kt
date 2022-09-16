package ao.co.proitconsulting.zoomunitel.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(
    tableName = "revistas"
)
data class RevistaModel(

    @PrimaryKey(autoGenerate = false)
    @SerializedName("IDREVISTA")
    var uid: Int,

    @SerializedName("NOME")
    var title: String,

    @SerializedName("DESCRICAO")
    var descricao: String,

    @SerializedName("FOTOKEY")
    var imgUrl: String,

    @SerializedName("PDFKEY")
    var pdfLink: String,

    @SerializedName("CATEGORIA")
    var categoria: String,

    @SerializedName("CREATEAT")
    var created_at: String

): Serializable
