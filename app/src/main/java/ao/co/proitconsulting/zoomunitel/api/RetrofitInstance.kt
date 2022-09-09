package ao.co.proitconsulting.zoomunitel.api

import ao.co.proitconsulting.zoomunitel.helpers.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitInstance {



    companion object{

        private val retrofit by lazy {
            val logging = HttpLoggingInterceptor()
            val tokenInterceptor = AddTokenInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            val client = OkHttpClient.Builder()
                .connectTimeout(Constants.REQUEST_TIMEOUT, TimeUnit.MILLISECONDS)
                .readTimeout(Constants.REQUEST_TIMEOUT, TimeUnit.MILLISECONDS)
                .writeTimeout(Constants.REQUEST_TIMEOUT, TimeUnit.MILLISECONDS)
                .addInterceptor(logging)
                .addInterceptor(tokenInterceptor)
                .build()

            Retrofit.Builder()
                .baseUrl(Constants.BASE_URL_ZOOM_UNITEL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
        }

        val api by lazy {
            retrofit.create(ZoomUnitelAPI::class.java)
        }
    }
}