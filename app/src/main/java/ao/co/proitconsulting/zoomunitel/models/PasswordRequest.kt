package ao.co.proitconsulting.zoomunitel.models

import com.google.gson.annotations.SerializedName

sealed class PasswordRequest{

    data class Email(
        @SerializedName("email")
        val email: String
    ) : PasswordRequest()

    data class Code(
        @SerializedName("email")
        val email: String,
        @SerializedName("codigo")
        val codigo: String
    ) : PasswordRequest()

    data class NewPass(
        @SerializedName("novapassword")
        val novapassword: String

    ) : PasswordRequest()
}

