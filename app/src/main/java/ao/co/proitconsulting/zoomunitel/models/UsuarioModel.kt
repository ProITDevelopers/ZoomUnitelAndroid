package ao.co.proitconsulting.zoomunitel.models


import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class UsuarioModel(

    @SerializedName("USERID")
    val userId:Int?,

    @SerializedName("NOME")
    val userNome:String?,

    @SerializedName("EMAIL")
    val userEmail:String?,

    @SerializedName("TELEFONE")
    val userPhone:String?,

    @SerializedName("FOTOKEY")
    val userPhoto:String?


) : Serializable {
    override fun toString(): String {
        return "Usuario(userId=$userId, userNome=$userNome, userEmail=$userEmail, userPhone=$userPhone, userPhoto=$userPhoto)"
    }
}


