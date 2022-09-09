package ao.co.proitconsulting.zoomunitel.api


import ao.co.proitconsulting.zoomunitel.models.LoginRequest
import ao.co.proitconsulting.zoomunitel.models.PasswordRequest
import ao.co.proitconsulting.zoomunitel.models.RegisterRequest
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.POST

interface ZoomUnitelAPI {


    @POST("/register")
    fun userRegister(
        @Body registerRequest: RegisterRequest?
    ) : retrofit2.Call<ResponseBody>


    @POST("/signin")
    fun userLogin(
        @Body loginRequest: LoginRequest?
    ) : retrofit2.Call<ResponseBody>

    @POST("/sendEmail")
    fun sendUserEmail(
        @Body passwordRequest: PasswordRequest?
    ) : retrofit2.Call<ResponseBody>

    @POST("/verifyCode")
    fun sendVerificationCode(
        @Body passwordRequest: PasswordRequest?
    ) : retrofit2.Call<ResponseBody>

    @POST("/resetPassWord")
    fun resetPassWord(
        @Body passwordRequest: PasswordRequest?
    ) : retrofit2.Call<ResponseBody>
}