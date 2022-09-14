package ao.co.proitconsulting.zoomunitel.models


import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class UsuarioModel(

    @SerializedName("userid")
    val userId:Long?,

    @SerializedName("nome")
    val userNome:String?,

    @SerializedName("email")
    val userEmail:String?,

    @SerializedName("telefone")
    val userPhone:String?,

    @SerializedName("imagem")
    val userPhoto:String?



) : Serializable {
    override fun toString(): String {
        return "Usuario(userId=$userId, userNome=$userNome, userEmail=$userEmail, userPhone=$userPhone, userPhoto=$userPhoto)"
    }
}


