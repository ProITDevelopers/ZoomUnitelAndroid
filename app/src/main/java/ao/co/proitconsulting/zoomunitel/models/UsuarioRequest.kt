package ao.co.proitconsulting.zoomunitel.models

import com.google.gson.annotations.SerializedName

sealed class UsuarioRequest{

    data class LoginRequest(
        @SerializedName("email")
        var email_Telefone: String,

        @SerializedName("password")
        val password: String
    ) : UsuarioRequest()

    data class RegisterRequest(
        @SerializedName("nome")
        val userNome:String?,

        @SerializedName("telefone")
        val userPhone:String?,

        @SerializedName("email")
        val userEmail:String?,

        @SerializedName("password")
        val userPassword:String?
    ) : UsuarioRequest()

    data class PassSendEmail(
        @SerializedName("email")
        val email: String
    ) : UsuarioRequest()

    data class PassSendCode(
        @SerializedName("email")
        val email: String,

        @SerializedName("codigo")
        val codigo: String
    ) : UsuarioRequest()

    data class PassSendNewPass(
        @SerializedName("novapassword")
        val novapassword: String
    ) : UsuarioRequest()

    data class UsuarioUpdateRequest(
        @SerializedName("nome")
        val userNome:String?,

        @SerializedName("telefone")
        val userPhone:String?,

        @SerializedName("email")
        val userEmail:String?

    ) : UsuarioRequest()

}
