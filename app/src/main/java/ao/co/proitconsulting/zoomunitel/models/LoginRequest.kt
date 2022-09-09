package ao.co.proitconsulting.zoomunitel.models

import com.google.gson.annotations.SerializedName

data class LoginRequest(

    @SerializedName("email")
    var email_Telefone: String,

    @SerializedName("password")
    val password: String
)
