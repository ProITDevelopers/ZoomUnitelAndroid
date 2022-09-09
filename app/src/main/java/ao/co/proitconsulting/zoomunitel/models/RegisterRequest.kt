package ao.co.proitconsulting.zoomunitel.models


import com.google.gson.annotations.SerializedName


data class RegisterRequest(

    @SerializedName("nome")
    val userNome:String?,

    @SerializedName("telefone")
    val userPhone:String?,

    @SerializedName("email")
    val userEmail:String?,

    @SerializedName("password")
    val userPassword:String?

)


