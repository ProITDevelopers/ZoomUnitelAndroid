package ao.co.proitconsulting.zoomunitel.api



import ao.co.proitconsulting.zoomunitel.models.RevistaModel
import ao.co.proitconsulting.zoomunitel.models.UsuarioRequest
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ZoomUnitelAPI {


    @POST("/register")
    fun userRegister(
        @Body registerRequest: UsuarioRequest.RegisterRequest?
    ) : Call<ResponseBody>


    @POST("/signin")
    fun userLogin(
        @Body loginRequest: UsuarioRequest.LoginRequest?
    ) : Call<ResponseBody>

    @POST("/sendEmail")
    fun sendUserEmail(
        @Body passSendEmail: UsuarioRequest.PassSendEmail?
    ) : Call<ResponseBody>

    @POST("/verifyCode")
    fun sendVerificationCode(
        @Body passSendCode: UsuarioRequest.PassSendCode?
    ) : Call<ResponseBody>

    @PUT("/resetPassWord")
    fun resetPassWord(
        @Body passSendNewPass: UsuarioRequest.PassSendNewPass?
    ) : Call<ResponseBody>

    @GET("/user/{id}")
    fun userProfile(
        @Path("id") userId: Int
    ) : Call<ResponseBody>

    @PUT("/user")
    fun userProfileUpdate(
        @Body userUpdateRequest: UsuarioRequest.UsuarioUpdateRequest?
    ) : Call<ResponseBody>

    @Multipart
    @PUT("/user/image")
    fun userPhotoUpdate(
        @Part photo: MultipartBody.Part
    ) : Call<ResponseBody>

    @GET("/revista")
    suspend fun getTodasRevistas() : List<RevistaModel>

    @Streaming
    @GET
    fun downloadPdfFile(@Url pdfUrl: String) : Call<ResponseBody>
}