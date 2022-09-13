package ao.co.proitconsulting.zoomunitel.api



import ao.co.proitconsulting.zoomunitel.models.RevistaResponse
import ao.co.proitconsulting.zoomunitel.models.UsuarioRequest
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ZoomUnitelAPI {


    @POST("/register")
    fun userRegister(
        @Body registerRequest: UsuarioRequest.RegisterRequest?
    ) : retrofit2.Call<ResponseBody>


    @POST("/signin")
    fun userLogin(
        @Body loginRequest: UsuarioRequest.LoginRequest?
    ) : retrofit2.Call<ResponseBody>

    @POST("/sendEmail")
    fun sendUserEmail(
        @Body passSendEmail: UsuarioRequest.PassSendEmail?
    ) : retrofit2.Call<ResponseBody>

    @POST("/verifyCode")
    fun sendVerificationCode(
        @Body passSendCode: UsuarioRequest.PassSendCode?
    ) : retrofit2.Call<ResponseBody>

    @POST("/resetPassWord")
    fun resetPassWord(
        @Body passSendNewPass: UsuarioRequest.PassSendNewPass?
    ) : retrofit2.Call<ResponseBody>

    @GET("/revista")
    suspend fun getTodasRevistas() : Response<RevistaResponse>
}