package ao.co.proitconsulting.zoomunitel.api



import ao.co.proitconsulting.zoomunitel.models.RevistaModel
import ao.co.proitconsulting.zoomunitel.models.UsuarioRequest
import okhttp3.ResponseBody
import retrofit2.http.*

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

    @GET("/user/{id}")
    fun userProfile(
        @Path("id") userId: Int
    ) : retrofit2.Call<ResponseBody>

    @PUT("/user")
    fun userProfileUpdate(
        @Body userUpdateRequest: UsuarioRequest.UsuarioUpdateRequest?
    ) : retrofit2.Call<ResponseBody>


    @GET("/revista")
    suspend fun getTodasRevistas() : List<RevistaModel>
}